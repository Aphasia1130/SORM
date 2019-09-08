package com.wink.test.vo;

public class EmpVO {
    private Integer id;
    private String empname;
    private Integer age;
    private String deptName;
    private String  deptAddr;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setEmpname(String empname) {
        this.empname = empname;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public void setDeptAddr(String deptAddr) {
        this.deptAddr = deptAddr;
    }

    public Integer getId() {
        return id;
    }

    public String getEmpname() {
        return empname;
    }

    public Integer getAge() {
        return age;
    }

    public String getDeptName() {
        return deptName;
    }

    public String getDeptAddr() {
        return deptAddr;
    }

    public EmpVO(Integer id, String empname, Integer age, String deptName, String deptAddr) {
        this.id = id;
        this.empname = empname;
        this.age = age;
        this.deptName = deptName;
        this.deptAddr = deptAddr;
    }

    public EmpVO() {
    }
}
