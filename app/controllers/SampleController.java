package controllers;

import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import services.Sample;

import java.util.List;

/**
 * Created by Onuche Akor Idoko on 1/30/17.
 */
public class SampleController extends Controller {

    @Transactional
    public Result indexProperties() {
       /* Sample.getPropertyIndexes();*/
        return ok("Done");
    }

}
