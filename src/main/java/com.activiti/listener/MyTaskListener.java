package com.activiti.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;

/*
监听器
 */
public class MyTaskListener implements TaskListener {
    /*
    指定负责人
     */
    @Override
    public void notify(DelegateTask delegateTask) {
        //判断当前任务是创建申请并且 事情名字是create
        if ("创建申请".equals(delegateTask.getName()) &&
                "create".equals(delegateTask.getEventName())) {
            delegateTask.setAssignee("张三");
        } else {
            System.out.println("有错误");
        }

    }
}
