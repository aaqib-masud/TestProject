package com.hp.uca.expert.vp.pd.Eagle_BTS_Outage_Huawei_NTDD;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.loader.csv.Loader;
import org.neo4j.loader.csv.Report;
import org.neo4j.loader.csv.utils.TmpDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.hp.uca.common.misc.Constants;
import com.hp.uca.expert.alarm.Alarm;
import com.hp.uca.expert.group.Group;
import com.hp.uca.expert.topology.TopoAccess;
import com.hp.uca.expert.vp.common.AbstractTest;

import junit.framework.JUnit4TestAdapter;

/**
 * The Class PropagationTest.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class UCA_Eagle_BTS_Outage_Huawei_NTDD extends AbstractTest {
	
	/**
	 * The log.
	 */
	private static Logger log = LoggerFactory.getLogger(UCA_Eagle_BTS_Outage_Huawei_NTDD.class);

	protected static final String ALARM_FILE_PREFIX = "src/test/resources/com/hp/uca/expert/vp/pd/Eagle_BTS_Outage_Huawei_NTDD/";
	private static final String ALARM_Trigger1= "Alarm_Eagle_BTS_Outage_Huawei_NTDD_Trigger1";
	private static final String ALARM_Trigger2= "Alarm_Eagle_BTS_Outage_Huawei_NTDD_Trigger2";
	private static final String ALARM_Sub1= "Alarm_Eagle_BTS_Outage_Huawei_NTDD_Sub1";
	private static final String ALARM_Sub2= "Alarm_Eagle_BTS_Outage_Huawei_NTDD_Sub2";
	private static final String ALARM_Sub3= "Alarm_Eagle_BTS_Outage_Huawei_NTDD_Sub3";
	private static final String ALARM_Sub4= "Alarm_Eagle_BTS_Outage_Huawei_NTDD_Sub4";
	private static final String ALARM_Trigger1_Clear= "Alarm_Eagle_BTS_Outage_Huawei_NTDD_Trigger1_clear";
	private static final String groupKEY1 = "<p>Problem_Eagle_BTS_Outage_Huawei_NTDD</p><e>lowermall_dcs-cth-ltrn4224-lre-u3098</e>";
	
	private static final String SCENARIO_BEAN_NAME = "com.hp.uca.expert.vp.pd.ProblemDetection";
	/**
	 * The Constant SCENARIO_PD_NAME.
	 */
	private static final String SCENARIO_PD_NAME = "com.hp.uca.expert.vp.pd.ProblemDetection";
	
	public UCA_Eagle_BTS_Outage_Huawei_NTDD() {
		super("test1");
		// TODO Auto-generated constructor stub
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info(Constants.TEST_START.val()
				+ UCA_Eagle_BTS_Outage_Huawei_NTDD.class.getName());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		log.info(Constants.TEST_END.val()
				+ UCA_Eagle_BTS_Outage_Huawei_NTDD.class.getName()
				+ Constants.GROUP_ALT1_SEPARATOR.val());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Override
	@Before
	public void setUp() throws Exception {
		log.info(Constants.TEST_START.val()
				+ UCA_Eagle_BTS_Outage_Huawei_NTDD.class.getName());
		
		super.setUp();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Override
	@After
	public void tearDown() throws Exception {
		log.info(Constants.TEST_END.val()
				+ UCA_Eagle_BTS_Outage_Huawei_NTDD.class.getName()
				+ Constants.GROUP_ALT1_SEPARATOR.val());
	}

	/**
	 * Way to run tests via ANT Junit
	 * 
	 * @return the JUnit4TestAdapter
	 */
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(UCA_Eagle_BTS_Outage_Huawei_NTDD.class);
	}


	/**
	 * @throws Exception
	 */
	@Test
	@DirtiesContext
	public void test() throws Exception {
		
		test_case1();
		
		//test_case2();
		
		//test_case3();
		
		//test_case4();
	}
	
	public void test_case1() throws Exception{
		try {
			
			//group name:
			this.sendAlarms(ALARM_FILE_PREFIX, ALARM_Trigger2 , true);
			Thread.sleep(5 * SECOND);
			this.sendAlarms(ALARM_FILE_PREFIX, ALARM_Trigger1 , true);
			this.sendAlarms(ALARM_FILE_PREFIX, ALARM_Sub1 , true);
			this.sendAlarms(ALARM_FILE_PREFIX, ALARM_Sub2 , true);
//			this.sendAlarms(ALARM_FILE_PREFIX, ALARM_Sub3 , true);
//			this.sendAlarms(ALARM_FILE_PREFIX, ALARM_Sub4 , true);
			
			Thread.sleep(60 * SECOND);
			
	//		this.sendAlarms(ALARM_FILE_PREFIX, ALARM_Trigger1_Clear , true);
			
	//		Thread.sleep(30 * SECOND);
			
			
			List<Group> gps = this.getGroupList(groupKEY1);
			Assert.assertFalse("no group found:" + groupKEY1, gps.isEmpty());
			Group gw =this.getGroupWithNotClearedPA(groupKEY1);
			Assert.assertNotNull("PA is cleared or filtered out for all group:" + groupKEY1, gw);
			Alarm pa = gw.getProblemAlarm();
			Assert.assertNotNull("problem alarm should be created now", pa);
			
			Assert.assertEquals(5, gw.getNumber());// total 
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {

			getScenario().getSession().dump();
			}
	}
	
	
	public void test_case2() throws Exception{
		try {
			
			//group name:
			this.sendAlarms(ALARM_FILE_PREFIX, ALARM_Trigger1 , true);
			this.sendAlarms(ALARM_FILE_PREFIX, ALARM_Sub1 , true);
			this.sendAlarms(ALARM_FILE_PREFIX, ALARM_Sub4 , true);
			
			Thread.sleep(40 * SECOND);
			
			List<Group> gps = this.getGroupList(groupKEY1);
			Assert.assertFalse("no group found:" + groupKEY1, gps.isEmpty());
			Group gw =this.getGroupWithNotClearedPA(groupKEY1);
			Assert.assertNotNull("PA is cleared or filtered out for all group:" + groupKEY1, gw);
			Alarm pa = gw.getProblemAlarm();
			Assert.assertNotNull("problem alarm should be created now", pa);
			
			Assert.assertEquals(3, gw.getNumber());// total 
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {

			getScenario().getSession().dump();
			}
	}
	
	
	public void test_case3() throws Exception{
		try {
			
			//group name:
			this.sendAlarms(ALARM_FILE_PREFIX, ALARM_Trigger2 , true);
			this.sendAlarms(ALARM_FILE_PREFIX, ALARM_Sub1 , true);
			this.sendAlarms(ALARM_FILE_PREFIX, ALARM_Sub4 , true);
			
			Thread.sleep(40 * SECOND);
			
			List<Group> gps = this.getGroupList(groupKEY1);
			Assert.assertFalse("no group found:" + groupKEY1, gps.isEmpty());
			Group gw =this.getGroupWithNotClearedPA(groupKEY1);
			Assert.assertNotNull("PA is cleared or filtered out for all group:" + groupKEY1, gw);
			Alarm pa = gw.getProblemAlarm();
			Assert.assertNotNull("problem alarm should be created now", pa);
			
			Assert.assertEquals(3, gw.getNumber());// total 
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {

			getScenario().getSession().dump();
			}
	}
	
	
	public void test_case4() throws Exception{
		try {
			
			//group name:
			this.sendAlarms(ALARM_FILE_PREFIX, ALARM_Trigger2 , true);
			this.sendAlarms(ALARM_FILE_PREFIX, ALARM_Sub4 , true);
			
			Thread.sleep(40 * SECOND);
			
			List<Group> gps = this.getGroupList(groupKEY1);
			Assert.assertFalse("no group found:" + groupKEY1, gps.isEmpty());
			Group gw =this.getGroupWithNotClearedPA(groupKEY1);
			Assert.assertNotNull("PA is cleared or filtered out for all group:" + groupKEY1, gw);
			Alarm pa = gw.getProblemAlarm();
			Assert.assertNotNull("problem alarm should be created now", pa);
			
			Assert.assertEquals(2, gw.getNumber());// total 
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {

			getScenario().getSession().dump();
			}
	}
	
}