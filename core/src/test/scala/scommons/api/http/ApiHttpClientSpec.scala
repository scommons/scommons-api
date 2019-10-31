package scommons.api.http

import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.{AsyncFlatSpec, Inside, Matchers}
import play.api.libs.json.Json.{stringify, toJson}
import play.api.libs.json._
import scommons.api.http.ApiHttpClient._
import scommons.api.http.ApiHttpClientSpec._
import scommons.api.http.ApiHttpData.{StringData, UrlEncodedFormData}
import scommons.api.http.ApiHttpMethod.GET

import scala.concurrent.Future
import scala.concurrent.duration._

class ApiHttpClientSpec extends AsyncFlatSpec
  with Matchers
  with Inside
  with AsyncMockFactory {

  private val baseUrl = "http://test.api.client"
  private val params = List("p1" -> "1", "p2" -> "2")
  private val headers = List("h1" -> "11", "h2" -> "22")
  private val timeout = 5.seconds
  private val defaultTimeout = 25.seconds

  private type HttpExecute =
    (String, String, List[(String, String)], List[(String, String)], Option[ApiHttpData], FiniteDuration) =>
      Future[Option[ApiHttpResponse]]

  private def stubExec = stubFunction[
    String, String, List[(String, String)], List[(String, String)], Option[ApiHttpData], FiniteDuration,
    Future[Option[ApiHttpResponse]]
    ]

  private class TestHttpClient(exec: HttpExecute) extends ApiHttpClient(baseUrl, defaultTimeout) {
    protected def execute(method: String,
                          targetUrl: String,
                          params: List[(String, String)],
                          headers: List[(String, String)],
                          data: Option[ApiHttpData],
                          timeout: FiniteDuration
                         ): Future[Option[ApiHttpResponse]] = {

      exec(method, targetUrl, params, headers, data, timeout)
    }
  }

  it should "fail if timeout when execute request" in {
    //given
    val url = "/api/get/url"
    val execute = stubExec
    val client = new TestHttpClient(execute)
    val data = UrlEncodedFormData(Map("test" -> Seq("test value")))

    execute.when(*, *, *, *, *, *).returns(Future.successful(None))

    //when
    client.exec(GET, url, Some(data), params, headers).failed.map { ex =>
      //then
      execute.verify("GET", s"$baseUrl$url", params, headers, Some(data), defaultTimeout)

      ex shouldBe ApiHttpTimeoutException(s"$baseUrl$url")

      val message = ex.getMessage
      message should include("Request timed out, unable to get timely response")
      message should include(url)
    }
  }

  it should "execute request and use defaultTimeout" in {
    //given
    val url = s"/api/get/url"
    val expectedResult = List(TestRespData(1, "test"))
    val expectedResponse = ApiHttpResponse(url, 200, Map.empty, stringify(toJson(expectedResult)))
    val execute = stubExec
    val client = new TestHttpClient(execute)

    execute.when(*, *, *, *, *, *).returns(Future.successful(Some(expectedResponse)))

    //when
    client.execGet[List[TestRespData]](url, params, headers).map { result =>
      //then
      execute.verify("GET", s"$baseUrl$url", params, headers, None, defaultTimeout)

      result shouldBe expectedResult
    }
  }

  it should "execute GET request" in {
    //given
    val url = s"/api/get/url"
    val expectedResult = List(TestRespData(1, "test"))
    val expectedResponse = ApiHttpResponse(url, 200, Map.empty, stringify(toJson(expectedResult)))
    val execute = stubExec
    val client = new TestHttpClient(execute)

    execute.when(*, *, *, *, *, *).returns(Future.successful(Some(expectedResponse)))

    //when
    client.execGet[List[TestRespData]](url, params, headers, timeout).map { result =>
      //then
      execute.verify("GET", s"$baseUrl$url", params, headers, None, timeout)

      result shouldBe expectedResult
    }
  }

  it should "execute POST request" in {
    //given
    val url = s"/api/post/url"
    val data = TestReqData(1)
    val expectedResult = List(TestRespData(2, "test"))
    val expectedResponse = ApiHttpResponse(url, 200, Map.empty, stringify(toJson(expectedResult)))
    val execute = stubExec
    val client = new TestHttpClient(execute)

    execute.when(*, *, *, *, *, *).returns(Future.successful(Some(expectedResponse)))

    //when
    client.execPost[TestReqData, List[TestRespData]](url, data, params, headers, timeout).map { result =>
      //then
      execute.verify("POST", s"$baseUrl$url", params, headers, Some(StringData(stringify(toJson(data)))), timeout)

      result shouldBe expectedResult
    }
  }

  it should "execute PUT request" in {
    //given
    val url = s"/api/put/url"
    val data = TestReqData(1)
    val expectedResult = List(TestRespData(2, "test"))
    val expectedResponse = ApiHttpResponse(url, 200, Map.empty, stringify(toJson(expectedResult)))
    val execute = stubExec
    val client = new TestHttpClient(execute)

    execute.when(*, *, *, *, *, *).returns(Future.successful(Some(expectedResponse)))

    //when
    client.execPut[TestReqData, List[TestRespData]](url, data, params, headers, timeout).map { result =>
      //then
      execute.verify("PUT", s"$baseUrl$url", params, headers, Some(StringData(stringify(toJson(data)))), timeout)

      result shouldBe expectedResult
    }
  }

  it should "execute DELETE request" in {
    //given
    val url = s"/api/delete/url"
    val data = TestReqData(1)
    val expectedResult = List(TestRespData(2, "test"))
    val expectedResponse = ApiHttpResponse(url, 200, Map.empty, stringify(toJson(expectedResult)))
    val execute = stubExec
    val client = new TestHttpClient(execute)

    execute.when(*, *, *, *, *, *).returns(Future.successful(Some(expectedResponse)))

    //when
    client.execDelete[TestReqData, List[TestRespData]](url, Some(data), params, headers, timeout).map { result =>
      //then
      execute.verify("DELETE", s"$baseUrl$url", params, headers, Some(StringData(stringify(toJson(data)))), timeout)

      result shouldBe expectedResult
    }
  }

  it should "fail if invalid json when parseResponse" in {
    //given
    val url = s"/some/url"
    val statusCode = 200
    val data = """{"id": 1, "missing": "name"}"""
    val response = ApiHttpResponse(url, statusCode, Map.empty, data)

    //when
    val ex = the[ApiHttpStatusException] thrownBy {
      ApiHttpClient.parseResponse[TestRespData](response)
    }

    //then
    inside(ex) { case ApiHttpStatusException(error, ApiHttpResponse(resUrl, status, resHeaders, body)) =>
      error shouldBe {
        "Fail to parse http response, error: List((/name,List(JsonValidationError(List(error.path.missing),WrappedArray()))))"
      }
      resUrl shouldBe url
      status shouldBe statusCode
      resHeaders shouldBe Map.empty
      body shouldBe data
    }
    
    val message = ex.getMessage
    message should include(s"url: $url")
    message should include(s"status: $statusCode")
    message should include("error.path.missing")
    message should include(s"body: $data")
  }

  it should "fail if error status with non-json when parseResponse" in {
    //given
    val url = s"/some/url"
    val statusCode = 400
    val data = "testData"
    val response = ApiHttpResponse(url, statusCode, Map.empty, data)

    //when
    val ex = the[ApiHttpStatusException] thrownBy {
      ApiHttpClient.parseResponse[List[TestRespData]](response)
    }

    //then
    inside(ex) { case ApiHttpStatusException(error, ApiHttpResponse(resUrl, status, resHeaders, body)) =>
      error shouldBe "Received error response"
      resUrl shouldBe url
      status shouldBe statusCode
      resHeaders shouldBe Map.empty
      body shouldBe data
    }
    
    val message = ex.getMessage
    message should include(s"url: $url")
    message should include(s"status: $statusCode")
    message should include(s"body: $data")
  }

  it should "parse json for HTTP success response when parseResponse" in {
    //given
    val url = "/api/url"
    val respData = List(TestRespData(1, "test"))
    val response = ApiHttpResponse(url, 200, Map.empty, stringify(toJson(respData)))

    //when
    val result = ApiHttpClient.parseResponse[List[TestRespData]](response)

    //then
    result shouldBe respData
  }

  it should "parse json for HTTP failure json response when parseResponse" in {
    //given
    val url = "/api/url"
    val respData = TestRespData(1, "test")
    val response = ApiHttpResponse(url, 500, Map.empty, stringify(toJson(respData)))

    //when
    val result = ApiHttpClient.parseResponse[TestRespData](response)

    //then
    result shouldBe respData
  }

  it should "convert list of optional params when queryParams" in {
    //when & then
    queryParams("test" -> None) shouldBe Nil
    queryParams("test" -> None, "test2" -> Some(2)) shouldBe List("test2" -> "2")
  }

  it should "return normalized target url when getTargetUrl" in {
    //when & then
    getTargetUrl("", "/some/url") shouldBe "/some/url"
    getTargetUrl("http://test.com", "some/url") shouldBe "http://test.com/some/url"
    getTargetUrl("http://test.com/", "some/url") shouldBe "http://test.com/some/url"
    getTargetUrl("http://test.com", "/some/url") shouldBe "http://test.com/some/url"
    getTargetUrl("http://test.com/", "/some/url") shouldBe "http://test.com/some/url"
  }
}

object ApiHttpClientSpec {

  case class TestReqData(id: Int)

  object TestReqData {
    implicit val jsonFormat: Format[TestReqData] = Json.format[TestReqData]
  }

  case class TestRespData(id: Int, name: String)

  object TestRespData {
    implicit val jsonFormat: Format[TestRespData] = Json.format[TestRespData]
  }
}
