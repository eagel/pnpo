package org.pnpo;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.pnpo.db.pool.DatabaseConnnectionPoolTests;

@RunWith(Suite.class)
@Suite.SuiteClasses({ DatabaseConnnectionPoolTests.class, TestPNPO.class })
public class TestMain {

}
