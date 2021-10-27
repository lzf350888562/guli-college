import request from '@/utils/request'  //固定  request里面封装了axios的方法

export default{
    //讲师列表 (条件查询分页)  匹配后端接口
    //current当前页 limit每页记录数 teacherQuery条件对象
    getTeacherListPage(current,limit,teacherQuery){
        return request({
            // url: '/edu/teacher/pageTeacherCondition/'+current+'/'+limit,
            url: `/eduservice/teacher/pageTeacherCondition/${current}/${limit}`,
            method: 'post',
            //teacherQuery条件对象,后端使用RequestBody获取
            //data表示把对象转换成json进行传递到接口
            data: teacherQuery
          })
    },
    deleteTeacherById(id){//删除讲师
        return request({
            url: `/eduservice/teacher/${id}`,
            method: 'delete'
          })
    },
    addTeacher(teacher){//添加讲师
        return request({
            url: `/eduservice/teacher/addTeacher`,
            method: 'post',
            data: teacher
          })
    },
    getTeacherInfo(id){//查询讲师
        return request({
            url: `/eduservice/teacher/getTeacher/${id}`,
            method: 'get'
          })
    },
    updateTeacherInfo(teacher){//修改讲师
        return request({
            url: `/eduservice/teacher/updateTeacher`,
            method: 'post',
            data: teacher
        })
    }
}
