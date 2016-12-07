package org.jfantasy.trade.boot;

import org.jfantasy.trade.bean.Project;
import org.jfantasy.trade.bean.enums.ProjectType;
import org.jfantasy.trade.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 添加默认的支付项目
 */
@Component
public class ProjectCommandLineRunner implements CommandLineRunner {

    private ProjectService projectService;

    @Override
    public void run(String... args) throws Exception {
        if (projectService.get(Project.PAYMENT) == null) {
            Project project = new Project(Project.PAYMENT);
            project.setName("支付");
            project.setDescription("业务订单的支付");
            project.setType(ProjectType.order);
            projectService.save(project);
        }
        if (projectService.get(Project.REFUND) == null) {
            Project project = new Project(Project.REFUND);
            project.setName("退款");
            project.setDescription("业务订单的退款");
            project.setType(ProjectType.order);
            projectService.save(project);
        }
        if (projectService.get(Project.INPOUR) == null) {
            Project project = new Project(Project.INPOUR);
            project.setName("充值");
            project.setDescription("充值卡");
            project.setType(ProjectType.deposit);
            projectService.save(project);
        }
        if (projectService.get(Project.INCOME) == null) {
            Project project = new Project(Project.INCOME);
            project.setName("收入");
            project.setDescription("收入");
            project.setType(ProjectType.transfer);
            projectService.save(project);
        }
        if (projectService.get(Project.WITHDRAWAL) == null) {
            Project project = new Project(Project.WITHDRAWAL);
            project.setName("提现");
            project.setDescription("用户提现");
            project.setType(ProjectType.withdraw);
            projectService.save(project);
        }
        if (projectService.get(Project.TRANSFER) == null) {
            Project project = new Project(Project.TRANSFER);
            project.setName("转账");
            project.setDescription("转账");
            project.setType(ProjectType.transfer);
            projectService.save(project);
        }
        if (projectService.get(Project.RECHARGE) == null) {
            Project project = new Project(Project.RECHARGE);
            project.setName("充值");
            project.setDescription("充值");
            project.setType(ProjectType.deposit);
            projectService.save(project);
        }
    }

    @Autowired
    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

}
