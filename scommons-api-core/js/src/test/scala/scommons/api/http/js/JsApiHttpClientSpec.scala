package scommons.api.http.js

import org.scalajs.dom
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.{AsyncFlatSpec, Matchers}
import scommons.api.http.ApiHttpResponse
import scommons.api.http.js.JsApiHttpClient.getFullUrl
import scommons.api.http.js.JsApiHttpClientSpec.MockXMLHttpRequest

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.scalajs.concurrent.JSExecutionContext
import scala.scalajs.js.annotation.JSExportAll

class JsApiHttpClientSpec extends AsyncFlatSpec
  with Matchers
  with AsyncMockFactory {

  implicit override def executionContext: ExecutionContext = JSExecutionContext.queue

  private val baseUrl = "http://test.api.client"

  private class TestJsClient(req: MockXMLHttpRequest, resp: MockXMLHttpRequest) extends JsApiHttpClient(baseUrl) {

    override private[js] def createRequest(): dom.XMLHttpRequest = req.asInstanceOf[dom.XMLHttpRequest]

    override private[js] def execute(req: dom.XMLHttpRequest, body: Option[String]): Future[dom.XMLHttpRequest] = {
      Future.successful(resp.asInstanceOf[dom.XMLHttpRequest])
    }
  }

  private val params = List("p1" -> "1", "p2" -> "2")
  private val timeout = 5.seconds

  it should "execute request without body" in {
    //given
    val targetUrl = s"$baseUrl/api/get/url"
    val body: Option[String] = None
    val expectedResult = ApiHttpResponse(200, "some resp body")
    val req = stub[MockXMLHttpRequest]
    val resp = stub[MockXMLHttpRequest]
    val client = new TestJsClient(req, resp)

    (req.open _).when(*, *).returns(())
    (req.timeout_= _).when(*).returns(())
    (resp.status _).when().returns(expectedResult.status)
    (resp.responseText _).when().returns(expectedResult.body)

    //when
    client.execute("GET", targetUrl, params, body, timeout).map { result =>
      //then
      (req.open _).verify("GET", getFullUrl(targetUrl, params))
      (req.timeout_= _).verify(timeout.toMillis)

      result shouldBe Some(expectedResult)
    }
  }

  it should "execute request with body" in {
    //given
    val targetUrl = s"$baseUrl/api/post/url"
    val body = Some("some req data")
    val expectedResult = ApiHttpResponse(200, "some resp body")
    val req = stub[MockXMLHttpRequest]
    val resp = stub[MockXMLHttpRequest]
    val client = new TestJsClient(req, resp)

    (req.open _).when(*, *).returns(())
    (req.timeout_= _).when(*).returns(())
    (req.setRequestHeader _).when(*, *).returns(())
    (resp.status _).when().returns(expectedResult.status)
    (resp.responseText _).when().returns(expectedResult.body)

    //when
    client.execute("POST", targetUrl, params, body, timeout).map { result =>
      //then
      (req.open _).verify("POST", getFullUrl(targetUrl, params))
      (req.timeout_= _).verify(timeout.toMillis)
      (req.setRequestHeader _).verify("Content-Type", "application/json")

      result shouldBe Some(expectedResult)
    }
  }

  it should "return None if timed out when execute request" in {
    //given
    val targetUrl = s"$baseUrl/api/get/url"
    val req = stub[MockXMLHttpRequest]
    val resp = stub[MockXMLHttpRequest]
    val client = new TestJsClient(req, resp)

    (req.open _).when(*, *).returns(())
    (req.timeout_= _).when(*).returns(())
    (resp.status _).when().returns(0)

    //when
    client.execute("GET", targetUrl, params, None, timeout).map { result =>
      //then
      (req.open _).verify("GET", getFullUrl(targetUrl, params))
      (req.timeout_= _).verify(timeout.toMillis)

      result shouldBe None
    }
  }

  it should "return full url when getFullUrl" in {
    //when & then
    getFullUrl("/test", Nil) shouldBe "/test"
    getFullUrl("/test", List("p" -> "")) shouldBe "/test?p="
    getFullUrl("/test", List("p" -> "1")) shouldBe "/test?p=1"
    getFullUrl("/test", List("p1" -> "1", "p2" -> "2")) shouldBe "/test?p1=1&p2=2"
    getFullUrl("/test", List("p" -> "1", "p" -> "2")) shouldBe "/test?p=2&p=1"
    getFullUrl("/test", List("p" -> "1", "p" -> "2", "p" -> "")) shouldBe "/test?p=&p=2&p=1"
    getFullUrl("/test", List("p" -> "1", "p" -> "2", "p3" -> "3")) shouldBe "/test?p=2&p=1&p3=3"
    //should encode param values
    getFullUrl("/test", List("p1" -> "1", "p2" -> "1 2")) shouldBe "/test?p1=1&p2=1%202"
    getFullUrl("/test", List("p1" -> "1", "p2" -> "1&2")) shouldBe "/test?p1=1&p2=1%262"
  }
}

object JsApiHttpClientSpec {

  @JSExportAll
  trait MockXMLHttpRequest {

    def open(method: String, url: String): Unit

    def timeout: Double

    def timeout_= (value: Double): Unit

    def setRequestHeader(header: String, value: String): Unit

    def status: Int

    def responseText: String
  }
}
