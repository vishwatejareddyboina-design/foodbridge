import pytest
import pandas as pd
import datetime
import os

class AppiumExcelReportPlugin:
    def __init__(self):
        self.results = []

    def pytest_runtest_logreport(self, report):
        if report.when == "call":
            test_id = len(self.results) + 1
            node_parts = report.nodeid.split("::")
            module = node_parts[0] if len(node_parts) > 0 else "Unknown"
            test_case_name = node_parts[-1] if len(node_parts) > 1 else "Unknown"
            status = "Pass" if report.passed else "Fail"
            exec_time = f"{report.duration:.2f}s"
            error = ""
            
            if report.failed:
                error = report.longreprtext.split('\n')[-1] if report.longreprtext else "Assertion Error"

            self.results.append({
                "Test ID": f"APP-TC-{test_id:03d}",
                "Module": "Android E2E",
                "Test Case Name": test_case_name,
                "Status": status,
                "Execution Time": exec_time,
                "Error": error
            })

if __name__ == "__main__":
    plugin = AppiumExcelReportPlugin()
    print("Starting Appium Android E2E Tests (100+ cases)...")
    
    # Run pytest programmatically
    pytest.main(["-v", "test_appium_suite.py"], plugins=[plugin])
    
    # Generate Excel
    if plugin.results:
        df = pd.DataFrame(plugin.results)
        timestamp = datetime.datetime.now().strftime("%Y-%m-%dT%H-%M-%S")
        filename = f"Appium_Android_Test_Report_{timestamp}.xlsx"
        
        # Save to current directory
        df.to_excel(filename, index=False, engine='openpyxl')
        print(f"\n=======================================================")
        print(f"Testing Complete! Generated report: {filename}")
        print(f"=======================================================\n")
    else:
        print("No test results were collected.")
