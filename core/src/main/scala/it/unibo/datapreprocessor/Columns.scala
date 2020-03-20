package it.unibo.datapreprocessor

object Columns {

  def getStrings = Seq("NewCreditCustomer",
    "Country",
    "EmploymentDurationCurrentEmployer",
    "ActiveScheduleFirstPaymentReached",
    "Rating",
    "Status",
    "Restructured",
    "WorseLateCategory"
  )

  def getUseless = Seq("LoanId",
    "UserName",
    "County",
    "City"
  )

  def getDate = Seq("ReportAsOfEOD",
    "ListedOnUTC",
    "BiddingStartedOn",
    "LoanApplicationStartedDate",
    "LoanDate",
    "ContractEndDate",
    "FirstPaymentDate",
    "MaturityDate_Original",
    "MaturityDate_Last",
    "DateOfBirth",
    "LastPaymentOn")

  def getBoolean = Seq("ActiveScheduleFirstPaymentReached",
    "NewCreditCustomer",
    "Restructured"
  )

  def getDouble = Seq("LoanNumber",
    "BidsPortfolioManager",
    "BidsApi",
    "BidsManual",
    "ApplicationSignedHour",
    "ApplicationSignedWeekday",
    "VerificationType",
    "LanguageCode",
    "Age",
    "Gender",
    "AppliedAmount",
    "Amount",
    "Interest",
    "LoanDuration",
    "MonthlyPayment",
    "UseOfLoan",
    "Education",
    "MaritalStatus",
    "EmploymentStatus",
    "OccupationArea",
    "HomeOwnershipType",
    "IncomeFromPrincipalEmployer",
    "IncomeFromPension",
    "IncomeFromFamilyAllowance",
    "IncomeFromSocialWelfare",
    "IncomeFromLeavePay",
    "IncomeFromChildSupport",
    "IncomeOther",
    "IncomeTotal",
    "ExistingLiabilities",
    "LiabilitiesTotal",
    "RefinanceLiabilities",
    "DebtToIncome",
    "FreeCash",
    "MonthlyPaymentDay",
    "PlannedInterestTillDate",
    "ExpectedLoss",
    "LossGivenDefault",
    "ExpectedReturn",
    "ProbabilityOfDefault",
    "ModelVersion",
    "PrincipalPaymentsMade",
    "InterestAndPenaltyPaymentsMade",
    "PrincipalBalance",
    "InterestAndPenaltyBalance",
    "NoOfPreviousLoansBeforeLoan",
    "AmountOfPreviousLoansBeforeLoan",
    "PrincipalOverdueBySchedule",
    "PreviousEarlyRepaymentsCountBeforeLoan"
  )

}
