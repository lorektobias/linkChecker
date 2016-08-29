package downlist
import rx.lang.scala.{Observer, observalbes.Blocking}


sealed trait Availablilty
case object Available extends Availablilty
case object Unavailable extends Availablilty

sealed trait Action
case object Checked extends Action
case object Fix extends Action
case object DownLong extends Action
case object None extends Action

sealed trait Duration
case object Never extends Duration
case object Now extends Duration
case object Forever extends Duration


case class Url(url: String)
case class Store(ftgid: Int, url: Url)
case class TestResult(store: Strong, availability: Availablilty, localReasource: Option[Url])
case class Decision(action: Action, duration: Option[Duration], store: Store)

object CheckLinks {
  def getPage: String = (new CurlClient()).request(downUrl).withAuth(StandardAuth).execute.getBody()
  def getStores(page: String): List[Store] = storePattern.findAllIn(page).matchData.map(match => Store(match(2), match(3)))
  def testStore(store: Store): Future[TestResult]
  def checkForPreviousDecision(result: TestResult): Future[(TestResult, Option[Decision])]
  def takeDecision(result: (TestResult, Option[Decision])): Decision = result match {
    case(_, Some(decision)) => decision
    case(testresult, _) => {
      val line = (result.availability, result.localReasource) match {
        case(Available, Some(Url(url))) => s"The page $result.store.url seems to be available, a local copy can be found here: $url"
        case(Unavailable, Some(Url(url))) => s"The page does not appear to be availabe, a local copy can be found here: $url"
      }
  def enactDecision(decision: Decision): Future[Unit]

  def main: Unit = {
    val futureStoredDecisions = getPage andThen getStores map testStore map ( ftr => ftr flatMap checkForPreviousDecision )
    val decisionStream = futureStoredDecisions.tail.foldLeft(Observable.from(futureStoredDecisions.head)){
      (stream, fsd) => stream.merge(Observable.from(fsd)
    }.toBlocking.forEach(
      { case((tr, od): (TestResult, Option[Decision])) => od match {
          case(None) => enactDecision(takeDecision(tr))
          case(Some(desision)) => enactDecisiond(desision)
        }
      }
    )
  }
}
