package Client

import io.circe.Json
import io.circe.parser._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class PublicDatasetTest extends AnyWordSpec with Matchers {

  "PublicDataset" should {
    "decode correctly empty payload" in {
      val incomingJson =
        parse(
          """{
                "PageSize": 1000,
                "PageNr": 100,
                "TotalCount": 10,
                "Count": 1,
                "Payload": [],
                "Success": false,
                "Error": "error"
              }""").getOrElse(Json.Null)

      PublicDataset.decoder.decodeJson(incomingJson) shouldBe Right(PublicDataset(
        PageSize = 1000,
        PageNr = 100,
        TotalCount = 10,
        Count = 1,
        Payload = Seq.empty[PublicDatasetPayload],
        Success = Some(false),
        Error = Some("error")
      ))
    }
  }

  "PublicDataset" should {
    "decode correctly full payload" in {
      val incomingJson =
        parse(
          """{
            |    "PageSize": 1000,
            |    "PageNr": 100,
            |    "TotalCount": 10,
            |    "Count": 1,
            |    "Payload": [
            |        {
            |            "LoanId": "2f915616-0700-4544-9fda-0fa764d5b732",
            |            "LoanNumber": 466350,
            |            "ListedOnUTC": "2016-02-12T16:24:08",
            |            "BiddingStartedOn": "2016-02-12T16:24:08",
            |            "BidsPortfolioManager": 1040,
            |            "BidsApi": 135,
            |            "BidsManual": 420,
            |            "UserName": "BO6KKK763",
            |            "NewCreditCustomer": true,
            |            "LoanApplicationStartedDate": "2016-02-12T16:03:17",
            |            "LoanDate": "2016-02-13T00:00:00",
            |            "ContractEndDate": "2021-05-25T00:00:00",
            |            "FirstPaymentDate": "2016-03-28T00:00:00",
            |            "MaturityDate_Original": "2021-02-25T00:00:00",
            |            "MaturityDate_Last": "2021-05-25T00:00:00",
            |            "ApplicationSignedHour": 9,
            |            "ApplicationSignedWeekday": 7,
            |            "VerificationType": 4,
            |            "LanguageCode": 3,
            |            "Age": 56,
            |            "DateOfBirth": "1959-05-19T00:00:00",
            |            "Gender": 0,
            |            "Country": "EE",
            |            "County": "HARJU",
            |            "City": "KEHRA",
            |            "AppliedAmount": 1595,
            |            "Amount": 1595,
            |            "Interest": 17.8,
            |            "LoanDuration": 60,
            |            "MonthlyPayment": 44,
            |            "UseOfLoan": 7,
            |            "Education": 2,
            |            "MaritalStatus": 1,
            |            "NrOfDependants": "0",
            |            "EmploymentStatus": 3,
            |            "EmploymentDurationCurrentEmployer": "UpTo2Years",
            |            "EmploymentPosition": "SpecialistOfficeWorker",
            |            "WorkExperience": "15To25Years",
            |            "OccupationArea": 6,
            |            "HomeOwnershipType": 1,
            |            "IncomeFromPrincipalEmployer": 490,
            |            "IncomeFromPension": 0,
            |            "IncomeFromFamilyAllowance": 0,
            |            "IncomeFromSocialWelfare": 0,
            |            "IncomeFromLeavePay": 0,
            |            "IncomeFromChildSupport": 0,
            |            "IncomeOther": 0,
            |            "IncomeTotal": 490,
            |            "ExistingLiabilities": 5,
            |            "RefinanceLiabilities": 2,
            |            "LiabilitiesTotal": 596,
            |            "DebtToIncome": 30.37,
            |            "FreeCash": 91.21,
            |            "MonthlyPaymentDay": 25,
            |            "ActiveScheduleFirstPaymentReached": true,
            |            "PlannedPrincipalTillDate": 175.85,
            |            "PlannedInterestTillDate": 748.31,
            |            "LastPaymentOn": "2019-11-07T00:00:00",
            |            "CurrentDebtDaysPrimary": 361,
            |            "DebtOccuredOn": "2019-02-25T00:00:00",
            |            "CurrentDebtDaysSecondary": 361,
            |            "DebtOccuredOnForSecondary": "2019-02-25T00:00:00",
            |            "ExpectedLoss": 0.050838881856190815,
            |            "LossGivenDefault": 0.58,
            |            "ExpectedReturn": 0.12719477292239037,
            |            "ProbabilityOfDefault": 0.090689717707936476,
            |            "DefaultDate": "2019-05-13T00:00:00",
            |            "PrincipalOverdueBySchedule": 337.73,
            |            "PlannedPrincipalPostDefault": 267.17,
            |            "PlannedInterestPostDefault": 97.27,
            |            "EAD1": 911.28,
            |            "EAD2": 226.67,
            |            "PrincipalRecovery": 3.66,
            |            "InterestRecovery": 0,
            |            "RecoveryStage": 2,
            |            "StageActiveSince": "2019-08-07T13:53:33",
            |            "ModelVersion": 2,
            |            "Rating": "B",
            |            "EL_V0": null,
            |            "Rating_V0": null,
            |            "EL_V1": null,
            |            "Rating_V1": null,
            |            "EL_V2": 0.050838881856190815,
            |            "Rating_V2": "B",
            |            "LoanCancelled": false,
            |            "Status": "Late",
            |            "Restructured": true,
            |            "ActiveLateCategory": "180+",
            |            "WorseLateCategory": "180+",
            |            "CreditScoreEsMicroL": null,
            |            "CreditScoreEsEquifaxRisk": null,
            |            "CreditScoreFiAsiakasTietoRiskGrade": "",
            |            "CreditScoreEeMini": "1000",
            |            "PrincipalPaymentsMade": 687.38,
            |            "InterestAndPenaltyPaymentsMade": 684.61,
            |            "PrincipalWriteOffs": 0,
            |            "InterestAndPenaltyWriteOffs": 0,
            |            "PrincipalDebtServicingCost": 0,
            |            "InterestAndPenaltyDebtServicingCost": 1.97,
            |            "PrincipalBalance": 907.62,
            |            "InterestAndPenaltyBalance": 174.65,
            |            "NoOfPreviousLoansBeforeLoan": 0,
            |            "AmountOfPreviousLoansBeforeLoan": 0,
            |            "PreviousRepaymentsBeforeLoan": 0,
            |            "PreviousEarlyRepaymentsBeforeLoan": 0,
            |            "PreviousEarlyRepaymentsCountBeforeLoan": 0,
            |            "GracePeriodStart": "2017-06-26T00:00:00",
            |            "GracePeriodEnd": "2017-09-25T00:00:00",
            |            "NextPaymentDate": null,
            |            "NextPaymentNr": 0,
            |            "NrOfScheduledPayments": 48,
            |            "ReScheduledOn": "2017-06-22T00:00:00"
            |        }
            |    ],
            |    "Success": true,
            |    "Errors": null
            |}""".stripMargin).getOrElse(Json.Null)

      val result = PublicDataset.decoder.decodeJson(incomingJson)
      val payload: Seq[PublicDatasetPayload] = result.getOrElse(PublicDatasetEmpty).Payload

      result shouldBe Right(PublicDataset(
        PageSize = 1000,
        PageNr = 100,
        TotalCount = 10,
        Count = 1,
        Payload = payload,
        Success = Some(true),
        Error = None
      ))
    }
  }
}
