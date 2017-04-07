import com.google.inject.AbstractModule;
import controllers.Singletons;
import formatters.FormattersProvider;
import play.data.format.Formatters;
import search.Searcher;
import services.CacheService;
import services.DBService;
import services.Starter;

public class Module extends AbstractModule {

    @Override
    public void configure() {
        bind(CacheService.class).asEagerSingleton();
        bind(Searcher.class).asEagerSingleton();
        bind(DBService.class).asEagerSingleton();
        bind(Singletons.class).asEagerSingleton();
        bind(Starter.class).asEagerSingleton();

        bind(Formatters.class).toProvider(FormattersProvider.class);
    }
}
