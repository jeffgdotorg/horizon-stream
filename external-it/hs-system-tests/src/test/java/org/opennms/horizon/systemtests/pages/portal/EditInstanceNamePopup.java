package org.opennms.horizon.systemtests.pages.portal;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class EditInstanceNamePopup {
    private final static SelenideElement popup = $("[data-ref-id='feather-dialog']");
    private final static SelenideElement nameInp = $("#edit-instance-name");
    private final static SelenideElement updateBtn = $("#submit-cloud\\ instance");
    private final static SelenideElement cancelBtn = $("#cancel-btn-cloud\\ instance");
    private final static SelenideElement closeBtn = $("[data-ref-id='dialog-close']");
    private final static SelenideElement errorTxt = $("[data-ref-id='feather-form-element-error']");

    public static void waitNewState(Condition condition) {
        popup.shouldBe(condition);
    }

    public static void setNewInstanceName(String instanceName) {
        nameInp.shouldBe(enabled).setValue("").sendKeys(instanceName);
    }

    public static void clickUpdateBtn() {
        updateBtn.shouldBe(enabled).click();
    }

    public static void clickCancelBtn() {
        cancelBtn.shouldBe(enabled).click();
    }

    public static void clickCloseBtn() {
        closeBtn.shouldBe(enabled).click();
    }

    public static void verifyErrorMessage(String errorMessage) {
        errorTxt.shouldHave(text(errorMessage));
    }
}
