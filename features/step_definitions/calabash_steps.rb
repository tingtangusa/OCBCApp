require 'calabash-android/calabash_steps'

Then %{I fill up form with my name as "$name", id type as "$id_type", id as "$id", mobile as "$mobile"} do |name, id_type, id, mobile|
  steps %{
		Then I enter "#{name}" into "Full Name"
		And I select "#{id_type}" from "ID Type"
		And I enter text "#{id}" into field with id "custId"
  	And I enter "#{mobile}" into "Mobile Number"
  	And I press the "OK" button
  }
end

Given %{I have already setup} do
	steps %{
		Given I see the text "Welcome"
    Then I fill up form with my name as "Jason", id type as "NRIC", id as "S9546551B", mobile as "96191234"
		Then I wait for progress
    Given I should see text containing "Successfully Setup"
    Then I press the "OK" button
	}
end
	
