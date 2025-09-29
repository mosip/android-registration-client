package regclient.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import io.mosip.testrig.apirig.testrunner.BaseTestCase;
import io.mosip.testrig.apirig.testrunner.OTPListener;
import regclient.api.AdminTestUtil;
import regclient.api.ArcConfigManager;
import regclient.api.FetchUiSpec;

public class TestRunner {
	
	public static String jarUrl = TestRunner.class.getProtectionDomain().getCodeSource().getLocation().getPath();
	
	public static void main(String[] args) {	
		io.mosip.testrig.apirig.testrunner.BaseTestCase.currentModule = "androidregclient";
		AdminTestUtil.initialize();
		BaseTestCase.ApplnURI = ArcConfigManager.getiam_apiinternalendpoint();
		OTPListener otpListener = new OTPListener();
		otpListener.run();	
		FetchUiSpec.getUiSpec("newProcess");
		io.mosip.testrig.apirig.testrunner.BaseTestCase.setRunContext(checkRunType(), jarUrl);
		io.mosip.testrig.apirig.testrunner.BaseTestCase.copymoduleSpecificAndConfigFile("config");
		io.mosip.testrig.apirig.utils.AdminTestUtil.init();
		FetchUiSpec.getBiometricDetails("individualBiometrics");
		System.out.println("BaseTestCase.ApplnURI : " + BaseTestCase.ApplnURI);
		AdminTestUtil.getPreRegistrationFlow();
		
		File homeDir = null;
		TestNG runner = new TestNG();
		if(!ArcConfigManager.gettestcases().equals("")) {
			XmlSuite suite = new XmlSuite();
			suite.setName("MySuite");
			suite.addListener("regclient.utils.EmailableReport");

			XmlClass addMachineDetails = new XmlClass("regclient.androidTestCases.AddMachineDetails");
			XmlClass initallLaunch = new XmlClass("regclient.androidTestCases.IntialLunch");
			XmlClass logintest = new XmlClass("regclient.androidTestCases.logintest");
			XmlClass newRegistrationAdult = new XmlClass("regclient.androidTestCases.NewRegistrationAdult");
			XmlClass newRegistrationAdultException = new XmlClass("regclient.androidTestCases.NewRegistrationAdultException");
			XmlClass newRegistrationInfant = new XmlClass("regclient.androidTestCases.NewRegistrationInfant");
			XmlClass newRegistrationMinor = new XmlClass("regclient.androidTestCases.NewRegistrationMinor");
			XmlClass newRegistrationMinorException = new XmlClass("regclient.androidTestCases.NewRegistrationMinorException");
			XmlClass updateMyUinInfant = new XmlClass("regclient.androidTestCases.UpdateMyUinInfant");
			XmlClass updateMyUinMinor = new XmlClass("regclient.androidTestCases.UpdateMyUinMinor");
			XmlClass updateMyUINUpdatebiometrics = new XmlClass("regclient.androidTestCases.UpdateMyUinUpdateBiometrics");
			XmlClass updateMyUINUpdateDemographicDetails = new XmlClass("regclient.androidTestCases.UpdateMyUINUpdateDemographicDetails");


			List<XmlClass> classes = new ArrayList<>();
			String[] Scenarionames=ArcConfigManager.gettestcases().split(",");
			for(String test:Scenarionames) {
				String Scenarioname=test.toLowerCase();

				if(Scenarioname.equalsIgnoreCase("addMachineDetails"))
					classes.add(addMachineDetails);

				if(Scenarioname.equalsIgnoreCase("initallLaunch")) 
					classes.add(initallLaunch);
				
				if(Scenarioname.equalsIgnoreCase("logintest")) 
					classes.add(logintest);
				
				if(Scenarioname.equalsIgnoreCase("newRegistrationAdult"))
					classes.add(newRegistrationAdult);

				if(Scenarioname.equalsIgnoreCase("newRegistrationAdultException"))
					classes.add(newRegistrationAdultException);

				if(Scenarioname.equalsIgnoreCase("newRegistrationInfant"))
					classes.add(newRegistrationInfant);

				if(Scenarioname.equalsIgnoreCase("newRegistrationMinor"))
					classes.add(newRegistrationMinor);

				if(Scenarioname.equalsIgnoreCase("newRegistrationMinorException"))
					classes.add(newRegistrationMinorException);

				if(Scenarioname.equalsIgnoreCase("updateMyUinInfant"))
					classes.add(updateMyUinInfant);

				if(Scenarioname.equalsIgnoreCase("updateMyUinMinor"))
					classes.add(updateMyUinMinor);

				if(Scenarioname.equalsIgnoreCase("updateMyUINUpdatebiometrics"))
					classes.add(updateMyUINUpdatebiometrics);

				if(Scenarioname.equalsIgnoreCase("updateMyUINUpdateDemographicDetails"))
					classes.add(updateMyUINUpdateDemographicDetails);

			}
			XmlTest test = new XmlTest(suite);
			test.setName("MyTest");
			test.setXmlClasses(classes);

			List<XmlSuite> suites = new ArrayList<>();
			suites.add(suite);

			runner.setXmlSuites(suites);

		}else {
			List<String> suitefiles = new ArrayList<String>();
				homeDir = new File("testng.xml");
			suitefiles.add(homeDir.getAbsolutePath());
			runner.setTestSuites(suitefiles);

		}
		System.getProperties().setProperty("testng.outpur.dir", "testng-report");
		runner.setOutputDirectory("testng-report");
		System.getProperties().setProperty("emailable.report2.name", "AndroidRegClient-" + BaseTestCase.environment + 
				 "-run-" + System.currentTimeMillis() + "-report.html");
		runner.run();
		otpListener.bTerminate=true;
		System.exit(0);
	}
	
	public static String getResourcePath() {
		if (checkRunType().equalsIgnoreCase("JAR")) {
			return new File(jarUrl).getParentFile().getAbsolutePath().toString()+"/resources/";
		} else if (checkRunType().equalsIgnoreCase("IDE")) {
			String path = System.getProperty("user.dir") +  "//src//main//resources";
			if (path.contains("test-classes"))
				path = path.replace("test-classes", "classes");
			return path;
		}
		return "Global Resource File Path Not Found";
	}
	
	public static String checkRunType() {
		if (TestRunner.class.getResource("TestRunner.class").getPath().toString().contains(".jar"))
			return "JAR";
		else
			return "IDE";
	}

}
