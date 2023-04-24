package org.opennms.horizon.systemtests.steps.portal;

import io.cucumber.java.en.But;
import io.cucumber.java.en.Then;
import org.opennms.horizon.systemtests.pages.portal.DeleteInstancePopup;

public class DeleteInstanceSteps {

    @Then("'Instance Delete Confirmation' popup appears")
    public void popupIdDisplayed() {
        DeleteInstancePopup.waitPopupIsDisplayed(true);
    }

    @Then("the IT Administrator enters {string} as an 'Instance Name'")
    public void setInstnanceName(String instanceName) {
        DeleteInstancePopup.setInstanceNameToConfirmationInput(instanceName);
    }

    @Then("click on 'DELETE' button to confirm deletion")
    public void clickDeleteBtn() {
        DeleteInstancePopup.clickOnDeleteBtn();
    }

    @Then("click on 'CANCEL' button to close the popup")
    public void clickCancelBtn() {
        DeleteInstancePopup.clickOnCancelBtn();
        DeleteInstancePopup.waitPopupIsDisplayed(false);
    }

    @Then("click on 'X' button to close the popup")
    public void clickCloseBtn() {
        DeleteInstancePopup.clickOnCloseBtn();
        DeleteInstancePopup.waitPopupIsDisplayed(false);
    }

    @But("sees error message {string} for 'Instance Name' field")
    public void verifyErrorMessage(String errorMessage) {
        DeleteInstancePopup.verifyErrorText(errorMessage);
    }

    @Then("error message for 'Instance Name' field disappears")
    public void verifyNoErrorMessage() {
        DeleteInstancePopup.verifyNoError();
    }
}
