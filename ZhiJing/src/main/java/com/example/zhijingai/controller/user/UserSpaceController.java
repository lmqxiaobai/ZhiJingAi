package com.example.zhijingai.controller.user;
import com.example.zhijingai.demo.constant.IpAddressConstant;
import com.example.zhijingai.demo.constant.SpaceConstant;
import com.example.zhijingai.entitys.dto.SpaceDTO;
import com.example.zhijingai.entitys.dto.SpacePageQueryDTO;
import com.example.zhijingai.entitys.entity.Space;
import com.example.zhijingai.mapper.SpaceMapper;
import com.example.zhijingai.result.PageResult;
import com.example.zhijingai.result.Result;
import com.example.zhijingai.service.SpaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/user/space")
@Api(tags = "用户个人空间管理相关接口")
@CrossOrigin(origins = IpAddressConstant.IPADDRESS_OPEN)
public class UserSpaceController {
    @Autowired
    private SpaceService spaceService;
    @Autowired
    private SpaceMapper spaceMapper;

    /**
     * 添加文件
     * @param spaceDTO
     * @return
     * @throws IOException
     */
    @PostMapping("/insert")
    @ApiOperation("添加文件")
    public Result insertText(@RequestBody SpaceDTO spaceDTO) throws IOException {
        log.info("添加文件:{}", spaceDTO);
        //执行添加方法
        spaceService.insertSpace(spaceDTO);
        return Result.success();
    }

    /**
     * 根据字段查询文件
     * @param spaceDTO
     * @return
     */
    @GetMapping("/select")
    @ApiOperation("根据字段查询文件")
    public Result select(SpaceDTO spaceDTO) {
        log.info("根据字段查询文件:{}", spaceDTO);

        Space space = new Space();
        BeanUtils.copyProperties(spaceDTO,space);
        //返回的一个集合，因为可能有多个符合条件的文件
        List<Space> textList = spaceService.select(space);
        return Result.success(textList);
    }


    /**
     * 由个人空间状态修改至回收站状态(通过传递过来的id集合去查询对应的状态并修改状态)
     * @param ids
     * @return
     */
    @GetMapping("/delete")
    @ApiOperation("从个人空间批量删除文件")
    public Result deleteList(@RequestParam List<Long> ids) {
        log.info("批量删除文件:{}", ids);
        spaceService.deleteList(ids);
        return Result.success();
    }

    /**
     * 由回收站状态修改至个人空间状态
     * @return
     */
    @GetMapping("/recover")
    @ApiOperation("找回个人空间文件")
    public Result recoverText(@RequestParam List<Long> ids) {
        log.info("批量找回文件:{}", ids);

        spaceService.recover(ids);
        return Result.success();
    }

    /**
     * 将草稿箱状态修改为回收站状态
     * @return
     */
    @GetMapping("/deleteDraft")
    @ApiOperation("从草稿箱批量删除文件")
    public Result deleteDraft(@RequestParam List<Long> ids) {
        log.info("将状态修改为回收站状态");
        spaceService.deleteList(ids);
        return Result.success();
    }

    /**
     * 将回收站状态修改为草稿箱状态
     * @return
     */
    @GetMapping("/draft")
    @ApiOperation("找回草稿箱文件")
    public Result draftText(@RequestParam List<Long> ids) {
        log.info("将回收站状态修改为草稿箱状态");
        spaceService.draft(ids);
        return Result.success();
    }


    /**
     * 根据id查询文件
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询文档")
    public Result<Space> selectById(@PathVariable Long id) {
        log.info("根据id查询文件: {}", id);

        //将id传给text类然后调用select方法查询即可
        Space space = Space.builder()
                .id(id)
                .build();
        //获取到的集合只有一个，所以传回该集合的第一个元素即可
        List<Space> spaces = spaceService.select(space);
        return Result.success(spaces.get(0));
    }

    /**
     * 修改文档信息
     * @return
     */
    // 代码逻辑要改
    @PutMapping("/update")
    @ApiOperation("修改文档信息")
    public Result updateText(@RequestBody Space space) {
        log.info("修改文档信息:{}", space);

        //直接执行修改方法
        spaceService.update(space);
        return Result.success();
    }


    /**
     * 分页查询文件
     * @return
     */
    // 这里参数值要改变，分页查询不用传这么多参数
    @GetMapping("/page")
    @ApiOperation("分页查询文件")
    public Result<PageResult> page(SpacePageQueryDTO spacePageQueryDTO) {
        log.info("分页查询文件");

        PageResult pageResult = spaceService.pageQuery(spacePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 彻底删除文件
     */
    @Scheduled(cron = "0 0 0 1/1 * ?") //设置每天凌晨1点定时触发查看是否存在超过三十天的文件
    public void processTimeOutText() {
        log.info("处于回收站超过三十天自动被删除");

        LocalDateTime time = LocalDateTime.now().minusMonths(1);

        //需要查询回收站中的文档最后一次修改时间，再加上三十天与现在的时间进行比较
        List<Space> spaceList = spaceMapper.getByUpdateTimeAndStatus(SpaceConstant.DELETE, time);

        //先判断集合是否存在
        if(spaceList != null && spaceList.size() > 0) {
            //存在，且全为超时文档，需要彻底删除
            spaceList.forEach(space -> {
                //执行删除方法
                spaceService.deleteFormTable(space.getId());
            });
        }
    }

    /**
     * 根据id彻底删除文件
     * @return
     */
    @DeleteMapping("/delete/{id}")
    @ApiOperation("根据id彻底删除文件")
    public Result deleteById(@PathVariable Long id) {
        log.info("根据id彻底删除文件：{}", id);

        spaceService.deleteFormTable(id);
        return Result.success();
    }
}
