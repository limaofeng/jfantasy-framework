/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.schedule.service;

import java.util.ArrayList;
import java.util.List;
import net.asany.jfantasy.framework.error.IgnoreException;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;

public class JobInfo {

  private JobKey key;
  private String name;
  private String group;
  private boolean running;
  private Class<Job> jobClass;
  private JobDataMap jobDataMap;
  private TriggerType type;
  private String cronExpression;
  private int rate;
  private int times;

  private List<TriggerInfo> triggerInfos = new ArrayList<TriggerInfo>();

  public JobInfo() {}

  public JobInfo(JobDetail jobDetail) {
    this.key = jobDetail.getKey();
    this.name = this.key.getName();
    this.group = this.key.getGroup();
    this.jobClass = (Class<Job>) jobDetail.getJobClass();
    this.jobDataMap = jobDetail.getJobDataMap();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getGroup() {
    return group;
  }

  public void setGroup(String group) {
    this.group = group;
  }

  public JobKey getKey() {
    return key;
  }

  public void setKey(JobKey key) {
    this.key = key;
  }

  public List<TriggerInfo> getTriggerInfos() {
    return triggerInfos;
  }

  public void setTriggerInfos(List<TriggerInfo> triggerInfos) {
    this.triggerInfos = triggerInfos;
  }

  public void addTriggerInfo(TriggerInfo triggerInfo) {
    this.triggerInfos.add(triggerInfo);
  }

  public boolean isRunning() {
    return running;
  }

  public void setRunning(boolean running) {
    this.running = running;
  }

  public Class<Job> getJobClass() {
    return jobClass;
  }

  public void setJobClass(Class<Job> jobClass) {
    this.jobClass = jobClass;
  }

  public JobDataMap getJobDataMap() {
    return jobDataMap;
  }

  public void setJobDataMap(JobDataMap jobDataMap) {
    this.jobDataMap = jobDataMap;
  }

  public TriggerType getType() {
    return type;
  }

  public void setType(TriggerType type) {
    this.type = type;
  }

  public String getCronExpression() {
    return cronExpression;
  }

  public void setCronExpression(String cronExpression) {
    this.cronExpression = cronExpression;
  }

  public int getRate() {
    return rate;
  }

  public void setRate(int rate) {
    this.rate = rate;
  }

  public int getTimes() {
    return times;
  }

  public void setTimes(int times) {
    this.times = times;
  }

  public void setClassName(String className) {
    try {
      this.jobClass = (Class<Job>) Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new IgnoreException(e.getMessage(), e);
    }
  }

  public String getId() {
    return this.getGroup() + "." + this.getName();
  }
}
