package edu.hunnu.eduservice.controller.front;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.hunnu.commonutils.JwtUtils;
import edu.hunnu.commonutils.R;
import edu.hunnu.commonutils.ordervo.CourseWebVoOrder;
import edu.hunnu.commonutils.ordervo.UcenterMemberOrder;
import edu.hunnu.eduservice.client.OrderClient;
import edu.hunnu.eduservice.client.UcenterClient;
import edu.hunnu.eduservice.entity.EduComment;
import edu.hunnu.eduservice.entity.EduCourse;
import edu.hunnu.eduservice.entity.chapter.ChapterVo;
import edu.hunnu.eduservice.entity.frontvo.CourseFrontVo;
import edu.hunnu.eduservice.entity.frontvo.CourseWebVo;
import edu.hunnu.eduservice.service.EduChapterService;
import edu.hunnu.eduservice.service.EduCommentService;
import edu.hunnu.eduservice.service.EduCourseService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/eduservice/coursefront")
public class CourseFrontController {

    @Autowired
    private EduCourseService courseService;

    @Autowired
    private EduChapterService chapterService;

    @Autowired
    private EduCommentService commentService;

    @Autowired
    private UcenterClient ucenterClient;

    @Autowired
    private OrderClient orderClient;

    // 1 ?????????????????????????????????
    @PostMapping("getFrontCourseList/{page}/{limit}")
    public R getFrontCourseList(@PathVariable long page, @PathVariable long limit,
                                @RequestBody(required = false) CourseFrontVo courseFrontVo){
        Page<EduCourse> pageParam = new Page<>(page,limit);
        Map<String, Object> map = courseService.getCourseFrontList(pageParam, courseFrontVo);

        return R.ok().data(map);
    }

    //2 ?????????????????????
    @GetMapping("getFrontCourseInfo/{courseId}")
    public R getFrontCourseInfo(@PathVariable String courseId, HttpServletRequest request){
        // ????????????id ??????sql????????????????????????
        CourseWebVo courseWebVo = courseService.getBaseCourseInfo(courseId);

        // ????????????id?????????????????????
        List<ChapterVo> chapterVideoList = chapterService.getChapterVideoByCourseId(courseId);

        // ????????????id?????????id??????????????????????????????????????????(????????????)
        String memberIdByJwtToken = JwtUtils.getMemberIdByJwtToken(request);
        System.out.println("??????????????????id:"+courseId);
        System.out.println("??????token??????????????????id:"+memberIdByJwtToken);
        boolean isBuy = orderClient.isBuyCourse(courseId, memberIdByJwtToken);
        System.out.println("???????????????????????????:"+isBuy);
//        boolean buyCount =true;

        return R.ok().data("courseWebVo",courseWebVo).data("chapterVideoList",chapterVideoList).data("isBuy",isBuy);

    }

    //????????????id??????????????????
    @ApiOperation(value = "??????????????????")
    @GetMapping("{page}/{limit}")
    public R index(
            @ApiParam(name = "page", value = "????????????", required = true)
            @PathVariable Long page,

            @ApiParam(name = "limit", value = "???????????????", required = true)
            @PathVariable Long limit,

            @ApiParam(name = "courseQuery", value = "????????????", required = false)
                    String courseId) {
        Page<EduComment> pageParam = new Page<>(page, limit);

        QueryWrapper<EduComment> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id",courseId);

        commentService.page(pageParam,wrapper);
        List<EduComment> commentList = pageParam.getRecords();

        Map<String, Object> map = new HashMap<>();
        map.put("items", commentList);
        map.put("current", pageParam.getCurrent());
        map.put("pages", pageParam.getPages());
        map.put("size", pageParam.getSize());
        map.put("total", pageParam.getTotal());
        map.put("hasNext", pageParam.hasNext());
        map.put("hasPrevious", pageParam.hasPrevious());
        return R.ok().data(map);
    }

    @ApiOperation(value = "????????????")
    @PostMapping("auth/save")
    public R save(@RequestBody EduComment comment, HttpServletRequest request) {
        String memberId = JwtUtils.getMemberIdByJwtToken(request);
        if(StringUtils.isEmpty(memberId)) {
            return R.error().code(28004).message("?????????");
        }
        comment.setMemberId(memberId);

        UcenterMemberOrder ucenterInfo = ucenterClient.getUcenterPay(memberId);

        comment.setNickname(ucenterInfo.getNickname());
        comment.setAvatar(ucenterInfo.getAvatar());

        commentService.save(comment);
        return R.ok();
    }

    // ????????????id??????????????????
    @PostMapping("getCourseInfoOrder/{id}")
    public CourseWebVoOrder getCourseInfoOrder(@PathVariable String id){
        CourseWebVo courseInfo = courseService.getBaseCourseInfo(id);
        CourseWebVoOrder courseWebVoOrder = new CourseWebVoOrder();
        BeanUtils.copyProperties(courseInfo, courseWebVoOrder);
        return courseWebVoOrder;
    }


}
