package org.sunbird.learner.actors;

import static akka.testkit.JavaTestKit.duration;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import java.util.HashMap;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.sunbird.actor.service.SunbirdMWService;
import org.sunbird.common.config.ApplicationConfigActor;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.ActorOperations;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.request.Request;
import org.sunbird.learner.util.DataCacheHandler;
import org.sunbird.learner.util.Util;

public class ApplicationConfigActorTest {

  private static ActorSystem system;
  private static final Props props = Props.create(ApplicationConfigActor.class);

  @BeforeClass
  public static void setUp() {
    SunbirdMWService.init();
    system = ActorSystem.create("system");
    Util.checkCassandraDbConnections(JsonKey.SUNBIRD);
  }

  @Test
  public void updateSystemSettings() {
    boolean dbPhoneUniqueValue = false;
    boolean dbEmailUniqueValue = false;
    dbPhoneUniqueValue =
        Boolean.parseBoolean(DataCacheHandler.getConfigSettings().get(JsonKey.PHONE_UNIQUE));
    dbPhoneUniqueValue =
        Boolean.parseBoolean(DataCacheHandler.getConfigSettings().get(JsonKey.EMAIL_UNIQUE));
    TestKit probe = new TestKit(system);
    ActorRef subject = system.actorOf(props);

    Request reqObj = new Request();
    reqObj.setOperation(ActorOperations.UPDATE_SYSTEM_SETTINGS.getValue());
    HashMap<String, Object> innerMap = new HashMap<>();
    innerMap.put(JsonKey.PHONE_UNIQUE, dbPhoneUniqueValue);
    innerMap.put(JsonKey.EMAIL_UNIQUE, dbEmailUniqueValue);
    reqObj.getRequest().put(JsonKey.DATA, innerMap);
    subject.tell(reqObj, probe.getRef());
    Response res = probe.expectMsgClass(duration("200 second"), Response.class);
    Assert.assertTrue(null != res.get(JsonKey.RESPONSE));
  }

  @Test
  public void testInvalidOperation() {
    TestKit probe = new TestKit(system);
    ActorRef subject = system.actorOf(props);

    Request reqObj = new Request();
    reqObj.setOperation("INVALID_OPERATION");

    subject.tell(reqObj, probe.getRef());
    ProjectCommonException exc = probe.expectMsgClass(ProjectCommonException.class);
    Assert.assertTrue(null != exc);
  }

  @Test
  public void testInvalidRequestData() {
    TestKit probe = new TestKit(system);
    ActorRef subject = system.actorOf(props);

    Response reqObj = new Response();

    subject.tell(reqObj, probe.getRef());
    ProjectCommonException exc = probe.expectMsgClass(ProjectCommonException.class);
    Assert.assertTrue(null != exc);
  }

  @Test
  public void testInvalidRequestData1() {
    TestKit probe = new TestKit(system);
    ActorRef subject = system.actorOf(props);
    Request reqObj = new Request();
    reqObj.setOperation(null);
    subject.tell(reqObj, probe.getRef());
    NullPointerException exc = probe.expectMsgClass(NullPointerException.class);
    Assert.assertTrue(null != exc);
  }
}
