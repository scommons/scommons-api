package scommons.api.http.xhr

import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.{Assertion, Inside}
import scommons.api.http.ApiHttpData.{StringData, UrlEncodedFormData}
import scommons.api.http.xhr.XhrApiHttpClient._
import scommons.api.http.xhr.XhrApiHttpClientSpec.MockXMLHttpRequest
import scommons.api.http.{ApiHttpData, ApiHttpResponse}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.scalajs.concurrent.JSExecutionContext
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportAll
import scala.scalajs.js.typedarray._

class XhrApiHttpClientSpec extends AsyncFlatSpec
  with Matchers
  with Inside
  with AsyncMockFactory {

  implicit override def executionContext: ExecutionContext = JSExecutionContext.queue

  private val baseUrl = "http://test.api.client"

  private class TestXhrClient(req: MockXMLHttpRequest) extends XhrApiHttpClient(baseUrl) {

    override private[xhr] def createRequest(): raw.XMLHttpRequest = req.asInstanceOf[raw.XMLHttpRequest]
  }

  private val params = List("p1" -> "1", "p2" -> "2")
  private val headers = List("h1" -> "11", "h2" -> "22")
  private val defTimeout = 5.seconds

  it should "execute request without body" in {
    //given
    val targetUrl = s"$baseUrl/api/get/url"
    val body: Option[ApiHttpData] = None
    val respHeaders = Map("test_header" -> Seq("test header value"))
    val expectedResult = ApiHttpResponse(targetUrl, 200, respHeaders, "some resp body")
    val open = mockFunction[String, String, Unit]
    val timeout = mockFunction[Double, Unit]
    val onreadystatechange = mockFunction[js.Function1[js.Object, _], Unit]
    val readyState = mockFunction[Int]
    val send = mockFunction[js.Any, Unit]
    val setRequestHeader = mockFunction[String, String, Unit]
    val status = mockFunction[Int]
    val getAllResponseHeaders = mockFunction[String]
    val responseText = mockFunction[String]
    val response = mockFunction[js.Any]
    val req = new MockXMLHttpRequest(
      open, timeout, onreadystatechange, readyState, send, setRequestHeader, status,
      getAllResponseHeaders, responseText, response
    )
    val client = new TestXhrClient(req)

    open.expects("GET", getFullUrl(targetUrl, params)).returns(())
    timeout.expects(defTimeout.toMillis).returns(())
    headers.foreach { case (name, value) =>
      setRequestHeader.expects(name, value)
    }
    onreadystatechange.expects(*).onCall { value: js.Function1[js.Object, _] =>
      value(null)
      ()
    }
    readyState.expects().returns(4)
    send.expects(().asInstanceOf[js.Any])
    status.expects().returns(expectedResult.status).twice()
    getAllResponseHeaders.expects().returns("test_header: test header value\r\n")
    responseText.expects().returns(expectedResult.body)
    response.expects().returns(expectedResult.bodyAsBytes.toArray.toTypedArray.buffer)

    //when
    client.execute("GET", targetUrl, params, headers, body, defTimeout).map(inside(_) { case Some(result) =>
      //then
      assertApiHttpResponse(result, expectedResult)
    })
  }

  it should "execute request with plain text body" in {
    //given
    val targetUrl = s"$baseUrl/api/post/url"
    val data = "some req data"
    val body = Some(StringData(data, "text/plain"))
    val respHeaders = Map("test_header" -> Seq("test header value"))
    val expectedResult = ApiHttpResponse(targetUrl, 200, respHeaders, "some resp body")
    val open = mockFunction[String, String, Unit]
    val timeout = mockFunction[Double, Unit]
    val onreadystatechange = mockFunction[js.Function1[js.Object, _], Unit]
    val readyState = mockFunction[Int]
    val send = mockFunction[js.Any, Unit]
    val setRequestHeader = mockFunction[String, String, Unit]
    val status = mockFunction[Int]
    val getAllResponseHeaders = mockFunction[String]
    val responseText = mockFunction[String]
    val response = mockFunction[js.Any]
    val req = new MockXMLHttpRequest(
      open, timeout, onreadystatechange, readyState, send, setRequestHeader, status,
      getAllResponseHeaders, responseText, response
    )
    val client = new TestXhrClient(req)

    open.expects("POST", getFullUrl(targetUrl, params)).returns(())
    timeout.expects(defTimeout.toMillis).returns(())
    (headers ++ Map("Content-Type" -> "text/plain")).foreach { case (name, value) =>
      setRequestHeader.expects(name, value)
    }
    onreadystatechange.expects(*).onCall { value: js.Function1[js.Object, _] =>
      value(null)
      ()
    }
    readyState.expects().returns(4)
    send.expects(data.asInstanceOf[js.Any])
    status.expects().returns(expectedResult.status).twice()
    getAllResponseHeaders.expects().returns("test_header: test header value\r\n")
    responseText.expects().returns(expectedResult.body)
    response.expects().returns(expectedResult.bodyAsBytes.toArray.toTypedArray.buffer)

    //when
    client.execute("POST", targetUrl, params, headers, body, defTimeout).map(inside(_) { case Some(result) =>
      //then
      assertApiHttpResponse(result, expectedResult)
    })
  }

  it should "execute request with json body" in {
    //given
    val targetUrl = s"$baseUrl/api/post/url"
    val data = """{"test": "json"}"""
    val body = Some(StringData(data))
    val respHeaders = Map("test_header" -> Seq("test header value"))
    val expectedResult = ApiHttpResponse(targetUrl, 200, respHeaders, "some resp body")
    val open = mockFunction[String, String, Unit]
    val timeout = mockFunction[Double, Unit]
    val onreadystatechange = mockFunction[js.Function1[js.Object, _], Unit]
    val readyState = mockFunction[Int]
    val send = mockFunction[js.Any, Unit]
    val setRequestHeader = mockFunction[String, String, Unit]
    val status = mockFunction[Int]
    val getAllResponseHeaders = mockFunction[String]
    val responseText = mockFunction[String]
    val response = mockFunction[js.Any]
    val req = new MockXMLHttpRequest(
      open, timeout, onreadystatechange, readyState, send, setRequestHeader, status,
      getAllResponseHeaders, responseText, response
    )
    val client = new TestXhrClient(req)

    open.expects("POST", getFullUrl(targetUrl, params)).returns(())
    timeout.expects(defTimeout.toMillis).returns(())
    (headers ++ Map("Content-Type" -> "application/json")).foreach { case (name, value) =>
      setRequestHeader.expects(name, value)
    }
    onreadystatechange.expects(*).onCall { value: js.Function1[js.Object, _] =>
      value(null)
      ()
    }
    readyState.expects().returns(4)
    send.expects(data.asInstanceOf[js.Any])
    status.expects().returns(expectedResult.status).twice()
    getAllResponseHeaders.expects().returns("test_header: test header value\r\n")
    responseText.expects().returns(expectedResult.body)
    response.expects().returns(expectedResult.bodyAsBytes.toArray.toTypedArray.buffer)

    //when
    client.execute("POST", targetUrl, params, headers, body, defTimeout).map(inside(_) { case Some(result) =>
      //then
      assertApiHttpResponse(result, expectedResult)
    })
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
    val open = mockFunction[String, String, Unit]
    val timeout = mockFunction[Double, Unit]
    val onreadystatechange = mockFunction[js.Function1[js.Object, _], Unit]
    val readyState = mockFunction[Int]
    val send = mockFunction[js.Any, Unit]
    val setRequestHeader = mockFunction[String, String, Unit]
    val status = mockFunction[Int]
    val getAllResponseHeaders = mockFunction[String]
    val responseText = mockFunction[String]
    val response = mockFunction[js.Any]
    val req = new MockXMLHttpRequest(
      open, timeout, onreadystatechange, readyState, send, setRequestHeader, status,
      getAllResponseHeaders, responseText, response
    )
    val client = new TestXhrClient(req)

    open.expects("POST", getFullUrl(targetUrl, params)).returns(())
    timeout.expects(defTimeout.toMillis).returns(())
    (headers ++ Map("Content-Type" -> "application/x-www-form-urlencoded")).foreach { case (name, value) =>
      setRequestHeader.expects(name, value)
    }
    onreadystatechange.expects(*).onCall { value: js.Function1[js.Object, _] =>
      value(null)
      ()
    }
    readyState.expects().returns(4)
    send.expects("param1=value1&param1=value2&param2=value3".asInstanceOf[js.Any])
    status.expects().returns(expectedResult.status).twice()
    getAllResponseHeaders.expects().returns("test_header: test header value\r\n")
    responseText.expects().returns(expectedResult.body)
    response.expects().returns(expectedResult.bodyAsBytes.toArray.toTypedArray.buffer)

    //when
    client.execute("POST", targetUrl, params, headers, body, defTimeout).map(inside(_) { case Some(result) =>
      //then
      assertApiHttpResponse(result, expectedResult)
    })
  }

  it should "return None if timed out when execute request" in {
    //given
    val targetUrl = s"$baseUrl/api/get/url"
    val open = mockFunction[String, String, Unit]
    val timeout = mockFunction[Double, Unit]
    val onreadystatechange = mockFunction[js.Function1[js.Object, _], Unit]
    val readyState = mockFunction[Int]
    val send = mockFunction[js.Any, Unit]
    val setRequestHeader = mockFunction[String, String, Unit]
    val status = mockFunction[Int]
    val getAllResponseHeaders = mockFunction[String]
    val responseText = mockFunction[String]
    val response = mockFunction[js.Any]
    val req = new MockXMLHttpRequest(
      open, timeout, onreadystatechange, readyState, send, setRequestHeader, status,
      getAllResponseHeaders, responseText, response
    )
    val client = new TestXhrClient(req)

    open.expects("GET", getFullUrl(targetUrl, params)).returns(())
    timeout.expects(defTimeout.toMillis).returns(())
    headers.foreach { case (name, value) =>
      setRequestHeader.expects(name, value)
    }
    onreadystatechange.expects(*).onCall { value: js.Function1[js.Object, _] =>
      value(null)
      ()
    }
    readyState.expects().returns(4)
    send.expects(().asInstanceOf[js.Any])
    status.expects().returns(0)

    //when
    client.execute("GET", targetUrl, params, headers, None, defTimeout).map { result =>
      //then
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

  it should "return Nil if response is null or undefined when getBodyAsBytes" in {
    //when & then
    getBodyAsBytes(null) shouldBe Nil
    getBodyAsBytes(js.undefined) shouldBe Nil
  }

  it should "return bytes if response is ArrayBuffer when getBodyAsBytes" in {
    //given
    val response: ArrayBuffer = "test data".getBytes.toTypedArray.buffer
    
    //when & then
    getBodyAsBytes(response) shouldBe "test data".getBytes
  }

  private def assertApiHttpResponse(result: ApiHttpResponse, expected: ApiHttpResponse): Assertion = {
    result.url shouldBe expected.url
    result.status shouldBe expected.status
    result.headers shouldBe expected.headers
    result.body shouldBe expected.body
    result.bodyAsBytes shouldBe expected.bodyAsBytes
  }
}

object XhrApiHttpClientSpec {

  @JSExportAll
  class MockXMLHttpRequest(
                            openMock: (String, String) => Unit,
                            timeoutMock: Double => Unit,
                            onreadystatechangeMock: js.Function1[js.Object, _] => Unit,
                            readyStateMock: () => Int,
                            sendMock: js.Any => Unit,
                            setRequestHeaderMock: (String, String) => Unit,
                            statusMock: () => Int,
                            getAllResponseHeadersMock: () => String,
                            responseTextMock: () => String,
                            responseMock: () => js.Any
                          ) {

    def open(method: String, url: String): Unit = openMock(method, url)

    def timeout_=(value: Double): Unit = timeoutMock(value)
    
    def onreadystatechange_=(value: js.Function1[js.Object, _]): Unit = onreadystatechangeMock(value)

    def readyState: Int = readyStateMock()

    def send(data: js.Any): Unit = sendMock(data)

    def setRequestHeader(header: String, value: String): Unit = setRequestHeaderMock(header, value)

    def status: Int = statusMock()

    def getAllResponseHeaders(): String = getAllResponseHeadersMock()

    def responseText: String = responseTextMock()

    def response: js.Any = responseMock()
  }
}
