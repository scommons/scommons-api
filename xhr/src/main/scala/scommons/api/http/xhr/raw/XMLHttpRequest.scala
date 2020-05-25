package scommons.api.http.xhr.raw

import scala.scalajs.js
import scala.scalajs.js.annotation.JSGlobal

@js.native
@JSGlobal
class XMLHttpRequest extends js.Object {

  /**
    * Initializes a request. This method is to be used from JavaScript code; to
    * initialize a request from native code, use openRequest()instead.
    *
    * MDN
    */
  def open(method: String, url: String, async: Boolean = js.native,
           user: String = js.native, password: String = js.native): Unit = js.native

  /**
    * The number of milliseconds a request can take before automatically being
    * terminated. A value of 0 (which is the default) means there is no timeout. Note: You
    * may not use a timeout for synchronous requests with an owning window.
    *
    * MDN
    */
  var timeout: Double = js.native

  var onreadystatechange: js.Function1[js.Object, _] = js.native

  /**
    * The state of the request: Value State Description 0 UNSENT open()has not been
    * called yet. 1 OPENED send()has not been called yet. 2 HEADERS_RECEIVED send() has
    * been called, and headers and status are available. 3 LOADING Downloading;
    * responseText holds partial data. 4 DONE The operation is complete.
    *
    * MDN
    */
  def readyState: Int = js.native

  /**
    * Sends the request. If the request is asynchronous (which is the default), this
    * method returns as soon as the request is sent. If the request is synchronous, this
    * method doesn't return until the response has arrived.
    *
    * MDN
    */
  def send(data: js.Any = js.native): Unit = js.native

  /**
    * Sets the value of an HTTP request header. You must call setRequestHeader()
    * after open(), but before send(). If this method is called several times with the
    * same header, the values are merged into one single request header.
    *
    * MDN
    */
  def setRequestHeader(header: String, value: String): Unit = js.native

  /**
    * The status of the response to the request. This is the HTTP result code (for example,
    * status is 200 for a successful request).
    *
    * MDN
    */
  def status: Int = js.native

  def getAllResponseHeaders(): String = js.native

  /**
    * The response to the request as text, or null if the request was unsuccessful or has
    * not yet been sent.
    *
    * MDN
    */
  def responseText: String = js.native

  /**
    * The response entity body according to responseType, as an ArrayBuffer, Blob,
    * Document, JavaScript object (for "json"), or string. This is null if the request is
    * not complete or was not successful.
    *
    * MDN
    */
  def response: js.Any = js.native
}
