package search;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import controllers.Utility;
import formatters.DateFormatter;
import org.apache.commons.lang3.time.DateUtils;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.filter.FilterAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsBuilder;
import org.elasticsearch.search.aggregations.metrics.cardinality.Cardinality;
import org.elasticsearch.search.aggregations.metrics.valuecount.ValueCount;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import pojos.Param;
import search.Aggregate;
import search.Filterer;
import search.PropertyIndex;
import search.SearchReq;
import search.SearchRes;

import javax.annotation.PreDestroy;
import javax.inject.Singleton;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.termQuery;
import static org.elasticsearch.search.aggregations.AggregationBuilders.*;


@Singleton
public class Searcher {
	private static final String INDEX = "property_index";
	private static final String TYPE = "property_type";
	private static final String A_TYPE = "agent_type";

	private static Client client;

	public Searcher() {
	}

    @PreDestroy
    public void destroy() {
        stop();
    }

    public static Client getClient() {
        return client;
    }

	public static void start(String host) {
		try {
			client = TransportClient.builder().build()
					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), 9300));
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void stop() {
		System.out.println("Stopping elasticsearch");
		client.close();
	}

    public static AgentIndex getAgent(Long id) {
        GetResponse response = client.prepareGet(INDEX, A_TYPE, id.toString())
                .execute()
                .actionGet();
        return jsonToAgentIndex(response.getSourceAsString());
    }

	public static SearchRes dashboard() {
		SearchRes res = new SearchRes();
		SearchRequestBuilder requestBuilder = client.prepareSearch(INDEX).setTypes(TYPE);

		Filterer filterer = Filterer.start();
		filterer.field("internal").is(true);

		requestBuilder.addAggregation(
			filter("inventory")
					.filter(filterer.get())
					.subAggregation(
							dateHistogram("inventory")
							.field("postedOn")
							.interval(DateHistogramInterval.MONTH)
							//.format("yyyy-MM-dd")
							.minDocCount(0)
							//.extendedBounds("2015-01-01", "2015-12-31")
					)
		);



		requestBuilder.addAggregation(getFilterAgg("status", filterer));
		requestBuilder.addAggregation(getFilterAgg("disposed", filterer));
		requestBuilder.addAggregation(getFilterAgg("similar", filterer));

		SearchResponse response = requestBuilder.execute().actionGet();

		Filter ag = response.getAggregations().get("inventory");
		Aggregation agg = ag.getAggregations().get("inventory");


		List<Aggregate> inventory = new LinkedList<>();
		Histogram histogram = (Histogram)agg;
		histogram.getBuckets().forEach(bucket -> {
			inventory.add(new Aggregate(bucket.getKeyAsString(), bucket.getDocCount()));
		});

		res.aggregates.put("inventory", inventory);
		res.aggregates.put("status", processAggregates(response, "status"));
		res.aggregates.put("disposed", processAggregates(response, "disposed"));
		res.aggregates.put("similar", processAggregates(response, "similar"));

		return res;
	}

	private static void addAggregates(SearchRequestBuilder requestBuilder, SearchReq req, String field, boolean admin){
		Filterer filterer = getFilterer(req, admin);
		if(filterer.has) {
			requestBuilder.addAggregation(getFilterAgg(field, filterer));
		} else {
			requestBuilder.addAggregation(getTermAgg(field));
		}
	}

	private static List<Aggregate> processAggregates(SearchResponse response, String field) {
		List<Aggregate> aggregates = new LinkedList<>();
		Aggregation agg = response.getAggregations().get(field);
		if(agg instanceof Filter) {
			Filter filter = (Filter)agg;
			Terms terms = filter.getAggregations().get(field);
			terms.getBuckets().forEach(bucket -> {
				aggregates.add(new Aggregate(bucket.getKeyAsString(), bucket.getDocCount()));
			});
		} else if(agg instanceof Terms) {
			Terms terms = (Terms)agg;
			terms.getBuckets().forEach(bucket -> {
				aggregates.add(new Aggregate(bucket.getKeyAsString(), bucket.getDocCount()));
			});
		}
		return  aggregates;
	}

	private static SortBuilder getSort(Param param){
		String sort = param.getSort();
		if(Utility.isNotEmpty(sort)) {

			if("price".equals(sort)) sort = "amount";
			if("amt".equals(sort)) sort = "amount";
			if("postedon".equals(sort)) sort = "postedOn";

			FieldSortBuilder sortBuilder = new FieldSortBuilder(sort);
			if ("desc".equals(param.getOrder())) {
				sortBuilder.order(SortOrder.DESC);
			} else {
				sortBuilder.order(SortOrder.ASC);
			}
			return sortBuilder;
		}
		return null;
	}

	private static FilterAggregationBuilder getFilterAgg(String field, Filterer filterer){
		return filter(field)
				.filter(filterer.get())
				.subAggregation(
						terms(field).field(field).order(Terms.Order.term(true)).size(Integer.MAX_VALUE)
				);
	}

	private static TermsBuilder getTermAgg(String field){
		return terms(field).field(field).order(Terms.Order.term(true)).size(Integer.MAX_VALUE);
	}


	public static Filterer getFilterer(SearchReq req, boolean admin) {
		Filterer filter = Filterer.start();
		if(Utility.isNotEmpty(req.mode)) {
			filter.field("mode", req.mode);
		}
		if(Utility.isNotEmpty(req.status)) {
			filter.field("status", req.status);
		}
		if(req.disposed != null) {
			if(req.disposed) {
				filter.field("disposed").is(true);
			} else {
				filter.field("disposed").is(false);
			}
		}

		if(req.discounted != null) {
			if(req.discounted) {
				filter.field("discounted").is(true);
			} else {
				filter.field("discounted").is(false);
			}
		}

		if(req.similar != null) {
			if(req.similar) {
				filter.field("similar").is(true);
			} else {
				filter.field("similar").is(false);
			}
		}

		if(!admin) {
			filter.field("duplicate").is(false);
		} else {
			if (req.nodup != null) {
				if (req.nodup) {
					filter.field("duplicate").is(false);
				} else {
					filter.field("duplicate").is(true);
				}
			}
		}

		if(req.managed != null) {
			if(req.managed) {
				filter.field("managed").is(true);
			} else {
				filter.field("managed").is(false);
			}
		}

		if(req.internal != null) {
			if(req.internal) {
				filter.field("internal").is(true);
			} else {
				filter.field("internal").is(false);
			}
		}

		if(req.crawled != null) {
			if(req.crawled) {
				filter.field("crawled").is(true);
			} else {
				filter.field("crawled").is(false);
			}
		}

		if(Utility.isNotEmpty(req.brief)) {
			filter.field("brief").is(req.brief);
		}
		if(req.agent != null) {
			filter.field("agent").is(req.agent);
		}
		if(req.poster != null) {
			filter.field("poster").is(req.poster);
		}
		if(Utility.isNotEmpty(req.state)) {
			filter.field("state", req.state);
		}
		if(Utility.isNotEmpty(req.area)) {
			filter.field("area", req.area);
		}
		if(Utility.isNotEmpty(req.axis)) {
            filter.field("axis", req.axis);
        }

		if(Utility.isNotEmpty(req.use)) {
			filter.field("use", req.use);
		}
		if(Utility.isNotEmpty(req.type)) {
			filter.field("type", req.type);
		}
		if(req.beds != null){
			filter.field("beds").is(req.beds);
		}
		if(req.baths != null) {
			filter.field("baths").is(req.baths);
		}
		if(req.min_price != null && req.max_price != null) {
			filter.field("amount").range(req.min_price, req.max_price);
		} else {
			if(req.max_price != null) {
				filter.field("amount").to(req.max_price);
			} else if(req.min_price != null) {
				filter.field("amount").from(req.min_price);
			}
		}

		try {
			if(Utility.isNotEmpty(req.start_date) && Utility.isNotEmpty(req.end_date)) {
				Date end_date = DateUtils.addDays(DateFormatter.convert(req.end_date), 1);
				filter.field("postedOn").range(
					DateFormatter.convert(req.start_date).getTime(),
					end_date.getTime()
				);
			} else if(Utility.isNotEmpty(req.start_date)) {
				filter.field("postedOn").from(DateFormatter.convert(req.start_date).getTime());
			} else if(Utility.isNotEmpty(req.end_date)) {
				Date end_date = DateUtils.addDays(DateFormatter.convert(req.end_date), 1);
				filter.field("postedOn").to(end_date.getTime());
			}
		} catch (Exception e) {}

		if(!req.faci.isEmpty()){
			filter.field("faci").in(req.faci);
		}

		return filter;
	}

	public static PropertyIndex jsonToPropertyIndex(String json) {
		ObjectMapper mapper = new ObjectMapper();
		PropertyIndex index = null;
		try {
			index = mapper.readValue(json, PropertyIndex.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return index;
	}

	public static Long getUniquePropertyRentedCountForAgent(Long agent) {
        SearchResponse sr = getClient().prepareSearch()
                .setQuery(QueryBuilders.boolQuery().must(termQuery("mode", "rent"))) //.must(termQuery("poster", agent)))
                .addAggregation(AggregationBuilders
                        .cardinality("total_unique_rented_property").field("pid"))
                .execute()
                .actionGet();
		Cardinality agg = sr.getAggregations().get("total_unique_rented_property");
		return agg.getValue();
	}

    public static Long getUniquePropertySoldCountForAgent(Long agent) {
        SearchResponse sr = getClient().prepareSearch()
                .setQuery(QueryBuilders.boolQuery().must(termQuery("mode", "sale")))//.must(termQuery("poster", agent)))
                .addAggregation(AggregationBuilders
                        .cardinality("total_unique_sold_property").field("pid"))
                .execute()
                .actionGet();
        Cardinality agg = sr.getAggregations().get("total_unique_sold_property");
        return agg.getValue();
    }

    public static Long getPropertyRentedForAgent(Long agent) {

        QueryBuilder qb = boolQuery()
                .must(termQuery("mode", "rent"))
                .must(termQuery("disposed", true));
                //.must(termQuery("poster", agent));

        SearchResponse sr = getClient().prepareSearch()
                .setQuery(qb)
                .addAggregation(AggregationBuilders
                        .count("total_property_rented").field("id"))
                .execute()
                .actionGet();
        ValueCount agg = sr.getAggregations().get("total_property_rented");
        return agg.getValue();
    }

    public static Long getPropertyAvailableForRentForAgent(Long agent) {

        QueryBuilder qb = boolQuery()
                .must(termQuery("mode", "rent"))
                .must(termQuery("disposed", false));
                //.must(termQuery("poster", agent));

        SearchResponse sr = getClient().prepareSearch()
                .setQuery(qb)
                .addAggregation(AggregationBuilders
                        .count("total_property_available_rent").field("id"))
                .execute()
                .actionGet();
        ValueCount agg = sr.getAggregations().get("total_property_available_rent");
        return agg.getValue();
    }

    public static Long getPropertySoldForAgent(Long agent) {

        QueryBuilder qb = boolQuery()
                .must(termQuery("mode", "sale"))
                .must(termQuery("disposed", true));
                //.must(termQuery("poster", agent));

        SearchResponse sr = getClient().prepareSearch()
                .setQuery(qb)
                .addAggregation(AggregationBuilders
                        .count("total_property_sold").field("id"))
                .execute()
                .actionGet();
        ValueCount agg = sr.getAggregations().get("total_property_sold");
        return agg.getValue();
    }

    public static Long getPropertyAvailableForSaleForAgent(Long agent) {

        QueryBuilder qb = boolQuery()
                .must(termQuery("mode", "sale"))
                .must(termQuery("disposed", false));
                //.must(termQuery("poster", agent));

        SearchResponse sr = getClient().prepareSearch()
                .setQuery(qb)
                .addAggregation(AggregationBuilders
                        .count("total_property_available_sale").field("id"))
                .execute()
                .actionGet();
        ValueCount agg = sr.getAggregations().get("total_property_available_sale");
        return agg.getValue();
    }

	public static List<Terms.Bucket> getPropertiesPerAreaForAgent(Long agent) {

		//QueryBuilder qb = boolQuery().must(termQuery("poster", agent));

		SearchResponse sr = getClient().prepareSearch()
				//.setQuery(qb)
				.addAggregation(AggregationBuilders
                        .terms("property_areas").field("axis").order(Terms.Order.count(false)).size(20))

				.execute()
				.actionGet();
		Terms agg = sr.getAggregations().get("property_areas");
		return agg.getBuckets();
	}

    public static AgentIndex jsonToAgentIndex(String json) {
        Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
        AgentIndex index = gson.fromJson(json, AgentIndex.class);
        return index;
    }

    public static class JsonDateDeserializer implements JsonDeserializer<Date> {
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Long l = json.getAsJsonPrimitive().getAsLong();
            Date d = new Date(l);
            return d;
        }
    }

}
