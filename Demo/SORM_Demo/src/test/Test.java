package test;

import com.wink.test.po.Emp;
import com.wink.test.vo.EmpVO;
import wink.sorm.core.Query;
import wink.sorm.core.QueryFactory;
import wink.sorm.core.TableContext;

import java.util.List;

public class Test {

    public static void add() {
        Emp e = new Emp();
        e.setAge(19);
        e.setEmpname("SROM0.1");
        e.setSalary(9000.0);
        Query q = QueryFactory.createQuery();
        q.insert(e);
    }

    public static void delete() {
        Emp e = new Emp();
        e.setAge(19);
        e.setEmpname("SROM0.1");
        e.setSalary(9000.0);
        e.setId(6);
        Query q = QueryFactory.createQuery();
        q.delete(e);
    }

    public static void select01() {
        Query q = QueryFactory.createQuery();
        Number number = q.queryNumber("select count(*) from emp where salary>?", new Object[]{100});
        System.out.println(number);
    }

    public static void select02() {
        Query q = QueryFactory.createQuery();
        Emp e = (Emp) q.queryUniqueRow("select * from emp where id=?", Emp.class, new Object[]{3});
        System.out.println(e.getEmpname());
    }

    public static void select03() {
        Query q = QueryFactory.createQuery();
        List<Emp> list = (List)q.queryRows("select * from emp where salary>?",Emp.class, new Object[]{6000});
        System.out.println(list);
        for (Emp emp : list) {
            System.out.println(emp.getEmpname());
        }
    }

    public static void select04() {
        Query q = QueryFactory.createQuery();
        String sql = "select e.id,e.empname,e.age,d.dname 'deptName',d.address 'deptAddr' from emp e join dept d on e.deptId=d.id";
        List<EmpVO> list = (List)q.queryRows(sql, EmpVO.class, new Object[]{});
        System.out.println(list);
        for (EmpVO empVO : list) {
            System.out.println(empVO.getEmpname()+"-->"+empVO.getDeptAddr()+"-->"+empVO.getDeptName());
        }
    }


    public static void select05() {
        Query q = QueryFactory.createQuery();
        Emp e = (Emp) q.queryById(Emp.class, 4);
        System.out.println(e.getEmpname());
    }


    public static void update(){
        Emp e = new Emp();
        e.setAge(19);
        e.setEmpname("高淇淇");
        e.setSalary(9000.0);
        e.setId(1);
        Query q = QueryFactory.createQuery();
        q.update(e,new String[]{"empname","salary"});
    }

    public static void main(String[] args) {
        //生成po类
//        TableContext.updateJavaPOFile();

        select05();
    }
}
