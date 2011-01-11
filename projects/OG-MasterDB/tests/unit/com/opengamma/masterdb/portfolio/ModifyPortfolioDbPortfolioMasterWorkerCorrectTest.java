/**
 * Copyright (C) 2009 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.masterdb.portfolio;

import static org.junit.Assert.assertEquals;

import java.util.TimeZone;

import javax.time.Instant;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opengamma.DataNotFoundException;
import com.opengamma.id.UniqueIdentifier;
import com.opengamma.master.portfolio.ManageablePortfolio;
import com.opengamma.master.portfolio.ManageablePortfolioNode;
import com.opengamma.master.portfolio.PortfolioDocument;
import com.opengamma.master.portfolio.PortfolioHistoryRequest;
import com.opengamma.master.portfolio.PortfolioHistoryResult;

/**
 * Tests ModifyPortfolioDbPortfolioMasterWorker.
 */
public class ModifyPortfolioDbPortfolioMasterWorkerCorrectTest extends AbstractDbPortfolioMasterWorkerTest {
  // superclass sets up dummy database

  private static final Logger s_logger = LoggerFactory.getLogger(ModifyPortfolioDbPortfolioMasterWorkerCorrectTest.class);

  public ModifyPortfolioDbPortfolioMasterWorkerCorrectTest(String databaseType, String databaseVersion) {
    super(databaseType, databaseVersion);
    s_logger.info("running testcases for {}", databaseType);
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }

  //-------------------------------------------------------------------------
  @Test(expected = IllegalArgumentException.class)
  public void test_correct_nullDocument() {
    _prtMaster.correct(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void test_correct_noPortfolioId() {
    ManageablePortfolio position = new ManageablePortfolio("Test");
    PortfolioDocument doc = new PortfolioDocument();
    doc.setPortfolio(position);
    _prtMaster.correct(doc);
  }

  @Test(expected = IllegalArgumentException.class)
  public void test_correct_noPortfolio() {
    PortfolioDocument doc = new PortfolioDocument();
    doc.setUniqueId(UniqueIdentifier.of("DbPrt", "201", "0"));
    _prtMaster.correct(doc);
  }

  @Test(expected = DataNotFoundException.class)
  public void test_correct_notFound() {
    ManageablePortfolio port = new ManageablePortfolio("Test");
    port.setUniqueId(UniqueIdentifier.of("DbPrt", "0", "0"));
    port.setRootNode(new ManageablePortfolioNode("Root"));
    PortfolioDocument doc = new PortfolioDocument(port);
    _prtMaster.correct(doc);
  }

  @Test(expected = IllegalArgumentException.class)
  public void test_correct_notLatestCorrection() {
    PortfolioDocument base = _prtMaster.get(UniqueIdentifier.of("DbPrt", "201", "0"));
    _prtMaster.correct(base);  // correction
    base = _prtMaster.get(UniqueIdentifier.of("DbPrt", "201", "0"));  // get old version
    _prtMaster.correct(base);  // cannot update old correction
  }

  @Test
  public void test_correct_getUpdateGet() {
    Instant now = Instant.now(_prtMaster.getTimeSource());
    
    UniqueIdentifier oldPortfolioId = UniqueIdentifier.of("DbPrt", "201", "0");
    PortfolioDocument base = _prtMaster.get(oldPortfolioId);
    ManageablePortfolio port = new ManageablePortfolio("NewName");
    port.setUniqueId(oldPortfolioId);
    port.setRootNode(base.getPortfolio().getRootNode());
    PortfolioDocument input = new PortfolioDocument(port);
    
    PortfolioDocument corrected = _prtMaster.correct(input);
    assertEquals(UniqueIdentifier.of("DbPrt", "201"), corrected.getUniqueId().toLatest());
    assertEquals(false, base.getUniqueId().getVersion().equals(corrected.getUniqueId().getVersion()));
    assertEquals(_version1Instant, corrected.getVersionFromInstant());
    assertEquals(_version2Instant, corrected.getVersionToInstant());
    assertEquals(now, corrected.getCorrectionFromInstant());
    assertEquals(null, corrected.getCorrectionToInstant());
    assertEquals(input.getPortfolio(), corrected.getPortfolio());
    
    PortfolioDocument old = _prtMaster.get(oldPortfolioId);
    assertEquals(base.getUniqueId(), old.getUniqueId());
    assertEquals(_version1Instant, old.getVersionFromInstant());
    assertEquals(_version2Instant, old.getVersionToInstant());  // old version ended
    assertEquals(_version1Instant, old.getCorrectionFromInstant());
    assertEquals(now, old.getCorrectionToInstant());
    assertEquals("TestPortfolio201", old.getPortfolio().getName());
    assertEquals("TestNode211", old.getPortfolio().getRootNode().getName());
    
    PortfolioDocument newer = _prtMaster.get(corrected.getUniqueId());
    assertEquals(corrected.getUniqueId(), newer.getUniqueId());
    assertEquals(_version1Instant, newer.getVersionFromInstant());
    assertEquals(_version2Instant, newer.getVersionToInstant());
    assertEquals(now, newer.getCorrectionFromInstant());
    assertEquals(null, newer.getCorrectionToInstant());
    assertEquals("NewName", newer.getPortfolio().getName());
    assertEquals("TestNode211", newer.getPortfolio().getRootNode().getName());
    assertEquals(old.getPortfolio().getRootNode().getUniqueId().toLatest(),
        newer.getPortfolio().getRootNode().getUniqueId().toLatest());
    assertEquals(false, old.getPortfolio().getRootNode().getUniqueId().getVersion().equals(
        newer.getPortfolio().getRootNode().getUniqueId().getVersion()));
    
    PortfolioHistoryRequest search = new PortfolioHistoryRequest(base.getUniqueId(), _version1Instant.plusSeconds(5), null);
    PortfolioHistoryResult searchResult = _prtMaster.history(search);
    assertEquals(2, searchResult.getDocuments().size());
    assertEquals(corrected.getUniqueId(), searchResult.getDocuments().get(0).getUniqueId());
    assertEquals(oldPortfolioId, searchResult.getDocuments().get(1).getUniqueId());
  }

  //-------------------------------------------------------------------------
  @Test
  public void test_toString() {
    assertEquals(_prtMaster.getClass().getSimpleName() + "[DbPrt]", _prtMaster.toString());
  }

}
