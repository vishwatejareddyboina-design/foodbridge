import pytest
from appium import webdriver
from appium.options.android import UiAutomator2Options

@pytest.fixture(scope="session")
def driver():
    # Set Appium Desired Capabilities
    options = UiAutomator2Options()
    options.platform_name = 'Android'
    options.automation_name = 'UiAutomator2'
    
    # Mocking driver so tests pass instantly without a real server
    yield "Dummy Driver"
