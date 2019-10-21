package scommons.api.http.ws

import java.util.concurrent.TimeoutException

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.mockito.MockitoSugar
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FlatSpec, Matchers}
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import play.api.libs.ws.{EmptyBody, InMemoryBody, StandaloneWSRequest, StandaloneWSResponse}
import scommons.api.http.ApiHttpResponse

import scala.concurrent.Future
import scala.concurrent.duration._

class WsApiHttpClientSpec extends FlatSpec
  with Matchers
  with BeforeAndAfterAll
  with BeforeAndAfterEach
  with MockitoSugar
  with ScalaFutures {

  implicit val defaultPatience: PatienceConfig = PatienceConfig(
    timeout = Span(5, Seconds),
    interval = Span(100, Millis)
  )

  private implicit val system: ActorSystem = ActorSystem(getClass.getSimpleName)
  private implicit val materializer: ActorMaterializer = ActorMaterializer()

  private val baseUrl = "http://test.api.client"
  private val response = mock[StandaloneWSResponse]

  private class TestWsClient extends WsApiHttpClient(baseUrl) {

    override private[ws] val ws = spy(StandaloneAhcWSClient())

    override private[ws] def execute(req: StandaloneWSRequest): Future[StandaloneWSResponse] = {
      Future.successful(response)
    }
  }

  private val client = spy(new TestWsClient())

  private val params = List("p1" -> "1", "p2" -> "2")
  private val headers = List("h1" -> "11", "h2" -> "22")
  private val timeout = 5.seconds

  override protected def beforeEach(): Unit = {
    reset(client, response)
  }

  override protected def afterEach(): Unit = {
    verifyNoMoreInteractions(response)
  }

  override protected def afterAll(): Unit = {
    reset(client.ws)

    system.terminate().futureValue

    verify(client.ws).close()
  }

  it should "execute request without body" in {
    //given
    val targetUrl = s"$baseUrl/api/get/url"
    val body: Option[String] = None
    val respHeaders = Map("test" -> Seq("test value"))
    val expectedResult = ApiHttpResponse(targetUrl, 200, respHeaders, "some resp body")
    when(response.status).thenReturn(expectedResult.status)
    when(response.headers).thenReturn(respHeaders)
    when(response.body).thenReturn(expectedResult.body)

    //when
    val result = client.execute("GET", targetUrl, params, headers, body, timeout).futureValue

    //then
    result shouldBe Some(expectedResult)

    assertRequest("GET", targetUrl, params, headers, body, timeout)

    verify(response).status
    verify(response).headers
    verify(response).body
    verifyNoMoreInteractions(response)
  }

  it should "execute request with body" in {
    //given
    val targetUrl = s"$baseUrl/api/post/url"
    val body = Some("some req data")
    val respHeaders = Map("test" -> Seq("test value"))
    val expectedResult = ApiHttpResponse(targetUrl, 200, respHeaders, "some resp body")
    when(response.status).thenReturn(expectedResult.status)
    when(response.headers).thenReturn(respHeaders)
    when(response.body).thenReturn(expectedResult.body)

    //when
    val result = client.execute("POST", targetUrl, params, headers, body, timeout).futureValue

    //then
    result shouldBe Some(expectedResult)

    assertRequest("POST", targetUrl, params, headers, body, timeout)

    verify(response).status
    verify(response).headers
    verify(response).body
    verifyNoMoreInteractions(response)
  }

  it should "return None if timed out when execute request" in {
    //given
    val targetUrl = s"$baseUrl/api/get/url"

    doReturn(Future.failed(new TimeoutException()))
      .when(client).execute(any[StandaloneWSRequest])

    //when
    val result = client.execute("GET", targetUrl, params, headers, None, timeout).futureValue

    //then
    result shouldBe None

    assertRequest("GET", targetUrl, params, headers, None, timeout)

    verifyZeroInteractions(response)
  }

  private def assertRequest(method: String,
                            targetUrl: String,
                            params: List[(String, String)],
                            headers: List[(String, String)],
                            body: Option[String],
                            timeout: FiniteDuration): Unit = {

    val reqCaptor = ArgumentCaptor.forClass(classOf[StandaloneWSRequest])
    verify(client).execute(reqCaptor.capture())

    val req = reqCaptor.getValue
    req.method shouldBe method
    req.url shouldBe targetUrl
    req.followRedirects shouldBe Some(true)
    req.requestTimeout shouldBe Some(timeout)
    req.queryString shouldBe params.foldLeft(Map.empty[String, Seq[String]]) {
      case (m, (k, v)) => m + (k -> (v +: m.getOrElse(k, Nil)))
    }
    headers.foreach { x =>
      req.header(x._1) shouldBe Some(x._2)
    }

    req.contentType shouldBe body.map(_ => "application/json")

    body match {
      case None => req.body shouldBe EmptyBody
      case Some(b) =>
        req.body.asInstanceOf[InMemoryBody].bytes.utf8String shouldBe b
    }
  }
}
