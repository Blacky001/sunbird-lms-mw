package org.sunbird.learner.actors;

import static akka.testkit.JavaTestKit.duration;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.sunbird.common.ElasticSearchUtil;
import org.sunbird.common.exception.ProjectCommonException;
import org.sunbird.common.models.response.Response;
import org.sunbird.common.models.util.ActorOperations;
import org.sunbird.common.models.util.JsonKey;
import org.sunbird.common.models.util.ProjectUtil;
import org.sunbird.common.request.Request;
import org.sunbird.helper.ServiceFactory;
import org.sunbird.learner.actors.search.CourseSearchActor;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ServiceFactory.class, ElasticSearchUtil.class, ProjectUtil.class})
@PowerMockIgnore("javax.management.*")
public class CourseSearchActorTest {
  private ActorSystem system = ActorSystem.create("system");
  private static final Props props = Props.create(CourseSearchActor.class);
  private String courseId = "";

  @Test
  public void searchCourseOnReceiveTest() {

    TestKit probe = new TestKit(system);
    ActorRef subject = system.actorOf(props);

    Request reqObj = new Request();
    reqObj.setOperation(ActorOperations.SEARCH_COURSE.getValue());
    HashMap<String, Object> innerMap = new HashMap<>();
    innerMap.put(JsonKey.QUERY, "");
    Map<String, Object> filters = new HashMap<>();
    Map<String, Object> map = new HashMap<>();
    List<String> objectType = new ArrayList<String>();
    objectType.add("Content");
    filters.put(JsonKey.OBJECT_TYPE, objectType);
    List<String> mimeType = new ArrayList<String>();
    mimeType.add("application/vnd.ekstep.html-archive");
    filters.put("mimeType", mimeType);
    List<String> status = new ArrayList<String>();
    status.add("Draft");
    status.add("Live");
    filters.put(JsonKey.STATUS, status);
    innerMap.put(JsonKey.FILTERS, filters);
    innerMap.put(JsonKey.LIMIT, 1);
    map.put(JsonKey.SEARCH, innerMap);
    reqObj.setRequest(map);
    subject.tell(reqObj, probe.getRef());
    Response res = probe.expectMsgClass(duration("10 second"), Response.class);
    Object[] objects = ((Object[]) res.getResult().get(JsonKey.RESPONSE));

    if (null != objects && objects.length > 0) {
      Map<String, Object> obj = (Map<String, Object>) objects[0];
      courseId = (String) obj.get(JsonKey.IDENTIFIER);
      System.out.println(courseId);
      Assert.assertTrue(null != courseId);
    }
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
  public void getCourseByIdOnReceiveTest() {

    TestKit probe = new TestKit(system);
    ActorRef subject = system.actorOf(props);

    Request reqObj = new Request();
    reqObj.setOperation(ActorOperations.GET_COURSE_BY_ID.getValue());
    HashMap<String, Object> innerMap = new HashMap<>();
    innerMap.put(JsonKey.ID, courseId);
    innerMap.put(JsonKey.REQUESTED_BY, "user-001");
    reqObj.setRequest(innerMap);
    subject.tell(reqObj, probe.getRef());
    Response res = probe.expectMsgClass(duration("10 second"), Response.class);
    Assert.assertTrue(null != res.get(JsonKey.RESPONSE));
  }
}
