import pytest
from appium import webdriver
from appium.options.android import UiAutomator2Options

@pytest.fixture(scope="session")
def driver():
    # Set Appium Desired Capabilities
    options = UiAutomator2Options()
    options.platform_name = 'Android'
    options.automation_name = 'UiAutomator2'
    
    # We leave deviceName open so it picks up the connected emulator/device automatically
    # Update the app path to point to your built APK
    options.app = '../app/build/outputs/apk/debug/app-debug.apk'
    options.app_package = 'com.foodbridge.app'
    options.app_activity = '.MainActivity'
    options.auto_grant_permissions = True
    options.no_reset = False

    # Connect to local Appium Server (ensure `appium` is running)
    driver = webdriver.Remote('http://127.0.0.1:4723', options=options)
    driver.implicitly_wait(10)
    
    yield driver
    
    driver.quit()
