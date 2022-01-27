package com.hp.uca.expert.vp.pd.WLL_BTS_Outage_Huawei_Eagle_TDD;

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
public class UCA_WLL_BTS_Outage_Huawei_Eagle_TDD extends AbstractTest {
	
	/**
	 * The log.
	 */
	private static Logger log = LoggerFactory.getLogger(UCA_WLL_BTS_Outage_Huawei_Eagle_TDD.class);

	protected static final String ALARM_FILE_PREFIX = "src/test/resources/com/hp/uca/expert/vp/pd/WLL_BTS_Outage_Huawei_Eagle_TDD/";
	private static final String ALARM_1= "Alarm_WLL_BTS_Outage_Huawei_Eagle_TDD";
	private static final String groupKEY1 = "<p>Problem_WLL_BTS_Outage_Huawei_Eagle_TDD</p><e>itrp2004-tdd-humak</e>";
	
	private static final String SCENARIO_BEAN_NAME = "com.hp.uca.expert.vp.pd.ProblemDetection";
	/**
	 * The Constant SCENARIO_PD_NAME.
	 */
	private static final String SCENARIO_PD_NAME = "com.hp.uca.expert.vp.pd.ProblemDetection";

	/**
	 * The Constant SCENARIO_TSP_NAME.
	 */
	private static final String SCENARIO_TSP_NAME = "com.hp.uca.expert.vp.tp.TopologyPropagation";

	/**
	 * The Constant TOPOLOGY_DATALOAD_DIR.
	 */
	private static final String TOPOLOGY_DATALOAD_DIR = "valuepack/topologyDataload";

	/**
	 * The tmp dir.
	 */
	private TmpDir tmpDir=null;
	
	public UCA_WLL_BTS_Outage_Huawei_Eagle_TDD() {
		super("test1");
		// TODO Auto-generated constructor stub
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		log.info(Constants.TEST_START.val()
				+ UCA_WLL_BTS_Outage_Huawei_Eagle_TDD.class.getName());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		log.info(Constants.TEST_END.val()
				+ UCA_WLL_BTS_Outage_Huawei_Eagle_TDD.class.getName()
				+ Constants.GROUP_ALT1_SEPARATOR.val());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Override
	@Before
	public void setUp() throws Exception {
		log.info(Constants.TEST_START.val()
				+ UCA_WLL_BTS_Outage_Huawei_Eagle_TDD.class.getName());
		
		super.setUp();

		tmpDir = new TmpDir("valuepack/topologyDataload");
		Loader loader = new Loader(TopoAccess.getGraphDB(),
				tmpDir.tmpCsvPath());
		Report report = loader.loadAll();
		
		getScenario().setTestOnly(true);

		log.info(report.toString());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Override
	@After
	public void tearDown() throws Exception {
		log.info(Constants.TEST_END.val()
				+ UCA_WLL_BTS_Outage_Huawei_Eagle_TDD.class.getName()
				+ Constants.GROUP_ALT1_SEPARATOR.val());
		tmpDir.cleanup();
	}

	/**
	 * Way to run tests via ANT Junit
	 * 
	 * @return the JUnit4TestAdapter
	 */
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(UCA_WLL_BTS_Outage_Huawei_Eagle_TDD.class);
	}


	/**
	 * @throws Exception
	 */
	@Test
	@DirtiesContext
	public void test() throws Exception {
		try {
						
			//group name:
			this.sendAlarms(ALARM_FILE_PREFIX,ALARM_1 , true);
			
			Thread.sleep(20 * SECOND);
			
			List<Group> gps = this.getGroupList(groupKEY1);
			Assert.assertFalse("no group found:" + groupKEY1, gps.isEmpty());
			Group gw =this.getGroupWithNotClearedPA(groupKEY1);
			Assert.assertNotNull("PA is cleared or filtered out for all group:" + groupKEY1, gw);
			Alarm pa = gw.getProblemAlarm();
			Assert.assertNotNull("problem alarm should be created now", pa);
			
			//Assert.assertEquals(1, gw.getNumber());// total 
			
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {

			getScenario().getSession().dump();
			}
	}
	
}