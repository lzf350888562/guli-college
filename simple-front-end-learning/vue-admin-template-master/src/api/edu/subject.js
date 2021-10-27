import request from '@/utils/request'  //固定  request里面封装了axios的方法

export default{
    //课程分类列表
    getSubjectList(){
        return request({
            url:'/eduservice/subject/getAllSubject',
            method:'get'
        })
    }
}