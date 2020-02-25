package Client

import io.circe.Decoder
import io.circe.generic.semiauto.deriveDecoder

sealed trait DataSetModelBase {

  def PageSize: Int

  def PageNr: Int

  def TotalCount: Int

  def Count: Int

  def Payload: Seq[PublicDatasetPayload]

  def Success: Option[Boolean]

  def Error: Option[String]
}

final case class PublicDataset(override val PageSize: Int,
                               override val PageNr: Int,
                               override val TotalCount: Int,
                               override val Count: Int,
                               override val Payload: Seq[PublicDatasetPayload],
                               override val Success: Option[Boolean],
                               override val Error: Option[String]) extends DataSetModelBase


final case class PublicDatasetPayload(LoanId: Option[String],
                                      LoanNumber: Option[Int],
                                      ListedOnUTC: Option[String],
                                      BiddingStartedOn: Option[String],
                                      BidsPortfolioManager: Option[Double],
                                      BidsApi: Option[Double],
                                      BidsManual: Option[Double],
                                      UserName: Option[String],
                                      NewCreditCustomer: Option[Boolean],
                                      LoanApplicationStartedDate: Option[String],
                                      LoanDate: Option[String],
                                      ContractEndDate: Option[String],
                                      FirstPaymentDate: Option[String],
                                      MaturityDate_Original: Option[String],
                                      MaturityDate_Last: Option[String],
                                      ApplicationSignedHour: Option[Int],
                                      ApplicationSignedWeekday: Option[Int],
                                      VerificationType: Option[Int],
                                      LanguageCode: Option[Int],
                                      Age: Option[Int],
                                      DateOfBirth: Option[String],
                                      Gender: Option[Int],
                                      Country: Option[String],
                                      County: Option[String],
                                      City: Option[String],
                                      AppliedAmount: Option[Double],
                                      Amount: Option[Double],
                                      Interest: Option[Double],
                                      LoanDuration: Option[Int],
                                      MonthlyPayment: Option[Int],
                                      UseOfLoan: Option[Int],
                                      Education: Option[Int],
                                      MaritalStatus: Option[Int],
                                      NrOfDependants: Option[Int],
                                      EmploymentStatus: Option[Int],
                                      EmploymentDurationCurrentEmployer: Option[String],
                                      EmploymentPosition: Option[String],
                                      WorkExperience: Option[String],
                                      OccupationArea: Option[Int],
                                      HomeOwnershipType: Option[Int],
                                      IncomeFromPrincipalEmployer: Option[Double],
                                      IncomeFromPension: Option[Double],
                                      IncomeFromFamilyAllowance: Option[Double],
                                      IncomeFromSocialWelfare: Option[Double],
                                      IncomeFromLeavePay: Option[Double],
                                      IncomeFromChildSupport: Option[Double],
                                      IncomeOther: Option[Double],
                                      IncomeTotal: Option[Double],
                                      ExistingLiabilities: Option[Int],
                                      RefinanceLiabilities: Option[Int],
                                      LiabilitiesTotal: Option[Double],
                                      DebtToIncome: Option[Double],
                                      FreeCash: Option[Double],
                                      MonthlyPaymentDay: Option[Int],
                                      ActiveScheduleFirstPaymentReached: Option[Boolean],
                                      PlannedPrincipalTillDate: Option[Double],
                                      PlannedInterestTillDate: Option[Double],
                                      LastPaymentOn: Option[String],
                                      CurrentDebtDaysPrimary: Option[Int],
                                      DebtOccuredOn: Option[String],
                                      CurrentDebtDaysSecondary: Option[Int],
                                      DebtOccuredOnForSecondary: Option[String],
                                      ExpectedLoss: Option[Double],
                                      LossGivenDefault: Option[Double],
                                      ExpectedReturn: Option[Double],
                                      ProbabilityOfDefault: Option[Double],
                                      DefaultDate: Option[String],
                                      PrincipalOverdueBySchedule: Option[Double],
                                      PlannedPrincipalPostDefault: Option[Double],
                                      PlannedInterestPostDefault: Option[Double],
                                      EAD1: Option[Double],
                                      EAD2: Option[Double],
                                      PrincipalRecovery: Option[Double],
                                      InterestRecovery: Option[Double],
                                      RecoveryStage: Option[Int],
                                      StageActiveSince: Option[String],
                                      ModelVersion: Option[Int],
                                      Rating: Option[String],
                                      EL_V0: Option[Double],
                                      Rating_V0: Option[Double],
                                      EL_V1: Option[Double],
                                      Rating_V1: Option[String],
                                      EL_V2: Option[Double],
                                      Rating_V2: Option[String],
                                      LoanCancelled: Option[Boolean],
                                      Status: Option[String],
                                      Restructured: Option[Boolean],
                                      ActiveLateCategory: Option[String],
                                      WorseLateCategory: Option[String],
                                      CreditScoreEsMicroL: Option[String],
                                      CreditScoreEsEquifaxRisk: Option[String],
                                      CreditScoreFiAsiakasTietoRiskGrade: Option[String],
                                      CreditScoreEeMini: Option[Double],
                                      PrincipalPaymentsMade: Option[Double],
                                      InterestAndPenaltyPaymentsMade: Option[Double],
                                      PrincipalWriteOffs: Option[Double],
                                      InterestAndPenaltyWriteOffs: Option[Double],
                                      PrincipalDebtServicingCost: Option[Double],
                                      InterestAndPenaltyDebtServicingCost: Option[Double],
                                      PrincipalBalance: Option[Double],
                                      InterestAndPenaltyBalance: Option[Double],
                                      NoOfPreviousLoansBeforeLoan: Option[Int],
                                      AmountOfPreviousLoansBeforeLoan: Option[Double],
                                      PreviousRepaymentsBeforeLoan: Option[Double],
                                      PreviousEarlyRepaymentsBeforeLoan: Option[Double],
                                      PreviousEarlyRepaymentsCountBeforeLoan: Option[Int],
                                      GracePeriodStart: Option[String],
                                      GracePeriodEnd: Option[String],
                                      NextPaymentDate: Option[String],
                                      NextPaymentNr: Option[Int],
                                      NrOfScheduledPayments: Option[Int],
                                      ReScheduledOn: Option[String])

case object PublicDatasetEmpty extends DataSetModelBase {

  override def PageSize: Int = 0

  override def PageNr: Int = 0

  override def TotalCount: Int = 0

  override def Count: Int = 0

  override def Payload: Seq[PublicDatasetPayload] = Seq.empty[PublicDatasetPayload]

  override def Success: Option[Boolean] = None

  override def Error: Option[String] = None
}

object PublicDatasetPayload {
  implicit val decoder: Decoder[PublicDatasetPayload] = deriveDecoder[PublicDatasetPayload]
}

object PublicDataset {
  implicit val decoder: Decoder[PublicDataset] = deriveDecoder[PublicDataset]
}