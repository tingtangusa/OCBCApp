Feature: Setup feature

  Scenario: As a valid user I can perform setup on my app
    Given I see the text "Welcome"
      Then I enter "Jason" into "Full Name"
      And I select "NRIC" from "ID Type"
      And I enter text "S9546551B" into field with id "custId"
      And I enter "96195678" into "Mobile Number"
      And I press the "OK" button
      Then I wait for "Successfully Setup" to appear
    Given I should see text containing "Successfully Setup"
      Then I press the "OK" button
