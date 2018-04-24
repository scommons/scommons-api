package scommons.api.http

import org.scalamock.scalatest.AsyncMockFactory
import org.scalatest.{AsyncFlatSpec, Matchers}
import play.api.libs.json.Json.{stringify, toJson}
import play.api.libs.json._
import scommons.api.http.ApiHttpClient._
import scommons.api.http.ApiHttpClientSpec._

import scala.concurrent.Future
import scala.concurrent.duration._

class ApiHttpClientSpec extends AsyncFlatSpec
  with Matchers
  with AsyncMockFactory {

  private val baseUrl = "http://test.api.client"
  private val params = List("p1" -> "1", "p2" -> "2")
  private val timeout = 5.seconds
  private val defaultTimeout = 25.seconds

  private type HttpExecute =
    (String, String, List[(String, String)], Option[String], FiniteDuration) => Future[Option[ApiHttpResponse]]

  private class TestHttpClient(exec: HttpExecute) extends ApiHttpClient(baseUrl, defaultTimeout) {
    protected def execute(method: String,
                          targetUrl: String,
                          params: List[(String, String)],
                          jsonBody: Option[String],
                          timeout: FiniteDuration
                         ): Future[Option[ApiHttpResponse]] = {

      exec(method, targetUrl, params, jsonBody, timeout)
    }
  }

  it should "execute request and use defaultTimeout" in {
    //given
    val url = s"/api/get/url"
    val expectedResult = List(TestRespData(1, "test"))
    val expectedResponse = ApiHttpResponse(200, stringify(toJson(expectedResult)))
    val execute = stubFunction[String, String, List[(String, String)], Option[String], FiniteDuration, Future[Option[ApiHttpResponse]]]
    val client = new TestHttpClient(execute)

    execute.when(*, *, *, *, *).returns(Future.successful(Some(expectedResponse)))

    //when
    client.execGet[List[TestRespData]](url, params).map { result =>
      //then
      execute.verify("GET", s"$baseUrl$url", params, None, defaultTimeout)

      result shouldBe expectedResult
    }
  }

  it should "execute GET request" in {
    //given
    val url = s"/api/get/url"
    val expectedResult = List(TestRespData(1, "test"))
    val expectedResponse = ApiHttpResponse(200, stringify(toJson(expectedResult)))
    val execute = stubFunction[String, String, List[(String, String)], Option[String], FiniteDuration, Future[Option[ApiHttpResponse]]]
    val client = new TestHttpClient(execute)

    execute.when(*, *, *, *, *).returns(Future.successful(Some(expectedResponse)))

    //when
    client.execGet[List[TestRespData]](url, params, timeout).map { result =>
      //then
      execute.verify("GET", s"$baseUrl$url", params, None, timeout)

      result shouldBe expectedResult
    }
  }

  it should "execute POST request" in {
    //given
    val url = s"/api/post/url"
    val data = TestReqData(1)
    val expectedResult = List(TestRespData(2, "test"))
    val expectedResponse = ApiHttpResponse(200, stringify(toJson(expectedResult)))
    val execute = stubFunction[String, String, List[(String, String)], Option[String], FiniteDuration, Future[Option[ApiHttpResponse]]]
    val client = new TestHttpClient(execute)

    execute.when(*, *, *, *, *).returns(Future.successful(Some(expectedResponse)))

    //when
    client.execPost[TestReqData, List[TestRespData]](url, data, params, timeout).map { result =>
      //then
      execute.verify("POST", s"$baseUrl$url", params, Some(stringify(toJson(data))), timeout)

      result shouldBe expectedResult
    }
  }

  it should "execute PUT request" in {
    //given
    val url = s"/api/put/url"
    val data = TestReqData(1)
    val expectedResult = List(TestRespData(2, "test"))
    val expectedResponse = ApiHttpResponse(200, stringify(toJson(expectedResult)))
    val execute = stubFunction[String, String, List[(String, String)], Option[String], FiniteDuration, Future[Option[ApiHttpResponse]]]
    val client = new TestHttpClient(execute)

    execute.when(*, *, *, *, *).returns(Future.successful(Some(expectedResponse)))

    //when
    client.execPut[TestReqData, List[TestRespData]](url, data, params, timeout).map { result =>
      //then
      execute.verify("PUT", s"$baseUrl$url", params, Some(stringify(toJson(data))), timeout)

      result shouldBe expectedResult
    }
  }

  it should "execute DELETE request" in {
    //given
    val url = s"/api/delete/url"
    val data = TestReqData(1)
    val expectedResult = List(TestRespData(2, "test"))
    val expectedResponse = ApiHttpResponse(200, stringify(toJson(expectedResult)))
    val execute = stubFunction[String, String, List[(String, String)], Option[String], FiniteDuration, Future[Option[ApiHttpResponse]]]
    val client = new TestHttpClient(execute)

    execute.when(*, *, *, *, *).returns(Future.successful(Some(expectedResponse)))

    //when
    client.execDelete[TestReqData, List[TestRespData]](url, Some(data), params, timeout).map { result =>
      //then
      execute.verify("DELETE", s"$baseUrl$url", params, Some(stringify(toJson(data))), timeout)

      result shouldBe expectedResult
    }
  }

  it should "fail if timeout when parseResponse" in {
    //given
    val url = s"/some/url"
    val client = new TestHttpClient(null)

    //when
    val ex = the[Exception] thrownBy {
      client.parseResponse[TestRespData](url, None)
    }

    //then
    val message = ex.getMessage
    message should include("Request timed out, unable to get timely response")
    message should include(url)
  }

  it should "fail if invalid json when parseResponse" in {
    //given
    val url = s"/some/url"
    val statusCode = 200
    val data = """{"id": 1, "missing": "name"}"""
    val response = ApiHttpResponse(statusCode, data)
    val client = new TestHttpClient(null)

    //when
    val ex = the[Exception] thrownBy {
      client.parseResponse[TestRespData](url, Some(response))
    }

    //then
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
    val response = ApiHttpResponse(statusCode, data)
    val client = new TestHttpClient(null)

    //when
    val ex = the[Exception] thrownBy {
      client.parseResponse[List[TestRespData]](url, Some(response))
    }

    //then
    val message = ex.getMessage
    message should include(s"url: $url")
    message should include(s"status: $statusCode")
    message should include(s"body: $data")
  }

  it should "parse json for HTTP success response when parseResponse" in {
    //given
    val respData = List(TestRespData(1, "test"))
    val response = ApiHttpResponse(200, stringify(toJson(respData)))
    val client = new TestHttpClient(null)

    //when
    val result = client.parseResponse[List[TestRespData]](s"/api/url", Some(response))

    //then
    result shouldBe respData
  }

  it should "parse json for HTTP failure json response when parseResponse" in {
    //given
    val respData = TestRespData(1, "test")
    val response = ApiHttpResponse(500, stringify(toJson(respData)))
    val client = new TestHttpClient(null)

    //when
    val result = client.parseResponse[TestRespData](s"/api/url", Some(response))

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
