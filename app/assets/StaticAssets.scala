package controllers

import play.mvc.Controller

/**
  * @author seyi
  */

object StaticAssets extends Controller {

  def getUrl(file: String): String = controllers.routes.Assets.versioned(file).toString

  def getImg(file: String): String = images + file

  var images = "https://s3.amazonaws.com/images.tolet.com.ng/"
}
