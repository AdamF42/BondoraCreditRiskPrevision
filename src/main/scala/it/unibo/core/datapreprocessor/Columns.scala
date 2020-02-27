package it.unibo.core.datapreprocessor

object Columns {

  def getStrings = Seq("NewCreditCustomer",
    "Country",
    "EmploymentDurationCurrentEmployer",
    "ActiveScheduleFirstPaymentReached",
    "Rating",
    "Status",
    "Restructured")

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

}
