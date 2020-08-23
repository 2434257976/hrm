package com.hrm.job.dao;

import com.hrm.commons.beans.Job;
import com.hrm.utils.PageModel;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

public interface IJobDao {
    //传参为map型
   /* @SelectProvider(type = JobProvider.class, method = "selectJob")
        //使用这个动态sql type后的类用来提供方法的返回值
    List<Job> selectJob(Map map);*/

   //传参为两个参数  参数前需要@Param 注解
    @SelectProvider(type = JobProvider.class, method = "selectJob")
    List<Job> selectJob(@Param("name") String name, @Param("pageModel") PageModel pageModel);

    @SelectProvider(type = JobProvider.class, method = "selectJobCount")
    int selectJobCount(String name);

    @Select("select * from job_inf where id=#{id}")
    Job selectJobById(int id);

    @Update("update job_inf set name=#{name} ,remark=#{remark} where id=#{id}")
    int updateJob(Job job);

    @DeleteProvider(type = JobProvider.class, method = "deleteJob")
    int deleteJob(@Param("ids") Integer[] ids);

    @Insert("insert into job_inf (name,remark) values (#{name},#{remark})")
    int insertJon(Job job);
}
