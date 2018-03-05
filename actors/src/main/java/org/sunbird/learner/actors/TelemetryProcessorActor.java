package org.sunbird.learner.actors;

import org.sunbird.common.request.Request;
import org.sunbird.telemetry.util.lmaxdisruptor.LMAXWriter;

import akka.actor.UntypedAbstractActor;

/**
 * Created by arvind on 8/1/18.
 */
public class TelemetryProcessorActor extends UntypedAbstractActor {

  private LMAXWriter lmaxWriter = LMAXWriter.getInstance();

  @Override
  public void onReceive(Object message) throws Throwable {

    if (message instanceof Request) {
      Request actorMessage = (Request) message;
      lmaxWriter.submitMessage(actorMessage);
    }

  }
}
