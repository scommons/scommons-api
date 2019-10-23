package scommons.api.http.dom

import org.scalajs.dom
import org.scalajs.dom.raw.Event
import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.{AsyncFlatSpec, Matchers}
import scommons.api.http.ApiHttpData.{StringData, UrlEncodedFormData}
import scommons.api.http.dom.DomApiHttpClient.getFullUrl
import scommons.api.http.dom.DomApiHttpClientSpec.MockXMLHttpRequest
import scommons.api.http.{ApiHttpData, ApiHttpResponse}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.scalajs.concurrent.JSExecutionContext
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportAll

class DomApiHttpClientSpec extends AsyncFlatSpec
  with Matchers
  with AsyncMockFactory {

  implicit override def executionContext: ExecutionContext = JSExecutionContext.queue

  private val baseUrl = "http://test.api.client"

  private class TestDomClient(req: MockXMLHttpRequest) extends DomApiHttpClient(baseUrl) {

    override private[dom] def createRequest(): dom.XMLHttpRequest = req.asInstanceOf[dom.XMLHttpRequest]
  }

  private val params = List("p1" -> "1", "p2" -> "2")
  private val headers = List("h1" -> "11", "h2" -> "22")
  private val timeout = 5.seconds

  it should "execute request without body" in {
    //given
    val targetUrl = s"$baseUrl/api/get/url"
    val body: Option[ApiHttpData] = None
    val respHeaders = Map("test_header" -> Seq("test header value"))
    val expectedResult = ApiHttpResponse(targetUrl, 200, respHeaders, "some resp body")
    val req = stub[MockXMLHttpRequest]
    val client = new TestDomClient(req)

    (req.open _).when(*, *).returns(())
    (req.timeout_= _).when(*).returns(())
    (req.setRequestHeader _).when(*, *).returns(())
    (req.onreadystatechange_= _).when(*).onCall { value: js.Function1[Event, _] =>
      value(null)
      ()
    }
    (req.readyState _).when().returns(4)
    (req.send _).when(*).returns(())
    (req.status _).when().returns(expectedResult.status)
    (req.getAllResponseHeaders _).when().returns("test_header: test header value\r\n")
    (req.responseText _).when().returns(expectedResult.body)

    //when
    client.execute("GET", targetUrl, params, headers, body, timeout).map { result =>
      //then
      (req.open _).verify("GET", getFullUrl(targetUrl, params))
      (req.timeout_= _).verify(timeout.toMillis)
      headers.foreach { x =>
        (req.setRequestHeader _).verify(x._1, x._2)
      }
      (req.send _).verify(().asInstanceOf[js.Any])

      result shouldBe Some(expectedResult)
    }
  }

  it should "execute request with plain text body" in {
    //given
    val targetUrl = s"$baseUrl/api/post/url"
    val data = "some req data"
    val body = Some(StringData(data, "text/plain"))
    val respHeaders = Map("test_header" -> Seq("test header value"))
    val expectedResult = ApiHttpResponse(targetUrl, 200, respHeaders, "some resp body")
    val req = stub[MockXMLHttpRequest]
    val client = new TestDomClient(req)

    (req.open _).when(*, *).returns(())
    (req.timeout_= _).when(*).returns(())
    (req.setRequestHeader _).when(*, *).returns(())
    (req.onreadystatechange_= _).when(*).onCall { value: js.Function1[Event, _] =>
      value(null)
      ()
    }
    (req.readyState _).when().returns(4)
    (req.send _).when(*).returns(())
    (req.status _).when().returns(expectedResult.status)
    (req.getAllResponseHeaders _).when().returns("test_header: test header value\r\n")
    (req.responseText _).when().returns(expectedResult.body)

    //when
    client.execute("POST", targetUrl, params, headers, body, timeout).map { result =>
      //then
      (req.open _).verify("POST", getFullUrl(targetUrl, params))
      (req.timeout_= _).verify(timeout.toMillis)
      (headers ++ Map("Content-Type" -> "text/plain")).foreach { x =>
        (req.setRequestHeader _).verify(x._1, x._2)
      }
      (req.send _).verify(data.asInstanceOf[js.Any])

      result shouldBe Some(expectedResult)
    }
  }

  it should "execute request with json body" in {
    //given
    val targetUrl = s"$baseUrl/api/post/url"
    val data = """{"test": "json"}"""
    val body = Some(StringData(data))
    val respHeaders = Map("test_header" -> Seq("test header value"))
    val expectedResult = ApiHttpResponse(targetUrl, 200, respHeaders, "some resp body")
    val req = stub[MockXMLHttpRequest]
    val client = new TestDomClient(req)

    (req.open _).when(*, *).returns(())
    (req.timeout_= _).when(*).returns(())
    (req.setRequestHeader _).when(*, *).returns(())
    (req.onreadystatechange_= _).when(*).onCall { value: js.Function1[Event, _] =>
      value(null)
      ()
    }
    (req.readyState _).when().returns(4)
    (req.send _).when(*).returns(())
    (req.status _).when().returns(expectedResult.status)
    (req.getAllResponseHeaders _).when().returns("test_header: test header value\r\n")
    (req.responseText _).when().returns(expectedResult.body)

    //when
    client.execute("POST", targetUrl, params, headers, body, timeout).map { result =>
      //then
      (req.open _).verify("POST", getFullUrl(targetUrl, params))
      (req.timeout_= _).verify(timeout.toMillis)
      (headers ++ Map("Content-Type" -> "application/json")).foreach { x =>
        (req.setRequestHeader _).verify(x._1, x._2)
      }
      (req.send _).verify(data.asInstanceOf[js.Any])

      result shouldBe Some(expectedResult)
    }
  }

  it should "execute request with form body" in {
    //given
    val targetUrl = s"$baseUrl/api/post/url"
    val body = Some(UrlEncodedFormData(Map(
      "param1" -> Seq("value1", "value2"),
      "param2" -> Seq("value3")
    )))
    val respHeaders = Map("test_header" -> Seq("test header value"))
    val expectedResult = ApiHttpResponse(targetUrl, 200, respHeaders, "some resp body")
    val req = stub[MockXMLHttpRequest]
    val client = new TestDomClient(req)

    (req.open _).when(*, *).returns(())
    (req.timeout_= _).when(*).returns(())
    (req.setRequestHeader _).when(*, *).returns(())
    (req.onreadystatechange_= _).when(*).onCall { value: js.Function1[Event, _] =>
      value(null)
      ()
    }
    (req.readyState _).when().returns(4)
    (req.send _).when(*).returns(())
    (req.status _).when().returns(expectedResult.status)
    (req.getAllResponseHeaders _).when().returns("test_header: test header value\r\n")
    (req.responseText _).when().returns(expectedResult.body)

    //when
    client.execute("POST", targetUrl, params, headers, body, timeout).map { result =>
      //then
      (req.open _).verify("POST", getFullUrl(targetUrl, params))
      (req.timeout_= _).verify(timeout.toMillis)
      (headers ++ Map("Content-Type" -> "application/x-www-form-urlencoded")).foreach { x =>
        (req.setRequestHeader _).verify(x._1, x._2)
      }
      (req.send _).verify("param1=value1&param1=value2&param2=value3".asInstanceOf[js.Any])

      result shouldBe Some(expectedResult)
    }
  }

  it should "return None if timed out when execute request" in {
    //given
    val targetUrl = s"$baseUrl/api/get/url"
    val req = stub[MockXMLHttpRequest]
    val client = new TestDomClient(req)

    (req.open _).when(*, *).returns(())
    (req.timeout_= _).when(*).returns(())
    (req.setRequestHeader _).when(*, *).returns(())
    (req.onreadystatechange_= _).when(*).onCall { value: js.Function1[Event, _] =>
      value(null)
      ()
    }
    (req.readyState _).when().returns(4)
    (req.send _).when(*).returns(())
    (req.status _).when().returns(0)

    //when
    client.execute("GET", targetUrl, params, headers, None, timeout).map { result =>
      //then
      (req.open _).verify("GET", getFullUrl(targetUrl, params))
      (req.timeout_= _).verify(timeout.toMillis)
      headers.foreach { x =>
        (req.setRequestHeader _).verify(x._1, x._2)
      }
      (req.send _).verify(().asInstanceOf[js.Any])

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

object DomApiHttpClientSpec {

  @JSExportAll
  trait MockXMLHttpRequest {

    def open(method: String, url: String): Unit

    def timeout_= (value: Double): Unit
    
    def onreadystatechange_= (value: js.Function1[Event, _]): Unit

    def readyState: Int

    def send(data: js.Any = ()): Unit

    def setRequestHeader(header: String, value: String): Unit

    def status: Int

    def getAllResponseHeaders(): String

    def responseText: String
  }
}
