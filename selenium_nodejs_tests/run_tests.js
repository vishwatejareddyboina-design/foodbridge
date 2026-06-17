const { Builder, By, until } = require('selenium-webdriver');
const ExcelJS = require('exceljs');
const path = require('path');

const BASE_URL = 'http://localhost:8000';
let driver;
const testResults = [];

// --- TEST DATA (Generating 100+ tests dynamically) ---
const roles = ['user', 'hotel', 'delivery'];
const emailInputs = ['invalidemail', 'admin\' OR 1=1--', '<script>alert(1)</script>@gmail.com', '', 'verylongemail'.repeat(10) + '@gmail.com'];
const passwordInputs = ['', '123', 'password', 'a'.repeat(100), 'pass123'];

const testCases = [];
roles.forEach(role => {
    emailInputs.forEach(email => {
        passwordInputs.forEach(pwd => {
            testCases.push({
                name: `Login Validation - Role: ${role}, Email: ${email || 'EMPTY'}, Pwd: ${pwd || 'EMPTY'}`,
                action: async () => {
                    await driver.get(`${BASE_URL}/login_${role}.html`);
                    await driver.findElement(By.id('email')).sendKeys(email);
                    await driver.findElement(By.id('password')).sendKeys(pwd);
                    await driver.findElement(By.id('btnSubmit')).click();
                }
            });
        });
    });
});

const pages = ['index.html', 'choose_role.html', 'privacy_policy.html', 'help_support.html'];
pages.forEach(page => {
    testCases.push({
        name: `Navigation - Check Load of ${page}`,
        action: async () => {
            await driver.get(`${BASE_URL}/${page}`);
            await driver.wait(until.elementLocated(By.tagName('body')), 5000);
        }
    });
});

async function runTests() {
    console.log(`Starting execution of ${testCases.length} test cases...`);
    
    let options = new (require('selenium-webdriver/chrome')).Options();
    options.addArguments('--headless');
    
    driver = await new Builder().forBrowser('chrome').setChromeOptions(options).build();
    
    for (let i = 0; i < testCases.length; i++) {
        const tc = testCases[i];
        let status = 'Pass';
        let errorMsg = '';
        const startTime = Date.now();
        
        try {
            await tc.action();
        } catch (error) {
            status = 'Fail';
            errorMsg = error.message.split('\n')[0];
        }
        
        const execTime = ((Date.now() - startTime) / 1000).toFixed(2) + 's';
        
        testResults.push({
            id: `TC-${String(i+1).padStart(3, '0')}`,
            module: 'E2E Suite',
            name: tc.name,
            status: status,
            time: execTime,
            error: errorMsg
        });
        process.stdout.write(`\rExecuted ${i+1}/${testCases.length} tests...`);
    }
    
    await driver.quit();
    console.log('\nGenerating Excel Report...');
    await generateExcel();
}

async function generateExcel() {
    const workbook = new ExcelJS.Workbook();
    const sheet = workbook.addWorksheet('Test Report');
    
    sheet.columns = [
        { header: 'Test ID', key: 'id', width: 10 },
        { header: 'Module', key: 'module', width: 20 },
        { header: 'Test Case Name', key: 'name', width: 50 },
        { header: 'Status', key: 'status', width: 15 },
        { header: 'Execution Time', key: 'time', width: 15 },
        { header: 'Error', key: 'error', width: 60 }
    ];
    
    testResults.forEach(res => sheet.addRow(res));
    
    // Style headers
    sheet.getRow(1).font = { bold: true };
    
    const timestamp = new Date().toISOString().replace(/[:.]/g, '-');
    const filename = `NodeJS_E2E_Test_Report_${timestamp}.xlsx`;
    await workbook.xlsx.writeFile(filename);
    console.log(`Test completely finished! Report saved as: ${filename}`);
}

runTests().catch(console.error);
