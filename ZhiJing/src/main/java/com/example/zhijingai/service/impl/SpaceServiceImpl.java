package com.example.zhijingai.service.impl;

import com.example.zhijingai.demo.constant.BaseConstant;
import com.example.zhijingai.demo.constant.SpaceConstant;
import com.example.zhijingai.entitys.dto.SpaceDTO;
import com.example.zhijingai.entitys.dto.SpacePageQueryDTO;
import com.example.zhijingai.entitys.entity.Space;
import com.example.zhijingai.entitys.vo.SpaceVO;
import com.example.zhijingai.mapper.SpaceMapper;
import com.example.zhijingai.result.PageResult;
import com.example.zhijingai.service.SpaceService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SpaceServiceImpl implements SpaceService {
    @Autowired
    private SpaceMapper spaceMapper;

    /**
     * 添加文件
     * @param spaceDTO
     */
    @Override
    public void insertSpace(SpaceDTO spaceDTO) {

        Space space = new Space();
        BeanUtils.copyProperties(spaceDTO,space);
        //获取到userId和当前时间赋值给text
        Long userId = BaseConstant.getCurrentId();
        LocalDateTime time = LocalDateTime.now();

        space.setUserId(userId);
        space.setCreateTime(time);
        space.setUpdateTime(time);
        space.setUpdateUser(userId);
        space.setCreateUser(userId);

        spaceMapper.insert(space);
    }

    /**
     * 批量删除文件
     * @param ids
     */
    @Transactional
    @Override
    public void deleteList(List<Long> ids) {

        for(Long id : ids) {
            // 这里代码逻辑要改变，通过id直接去批量修改状态值
            Space space = new Space();
            space.setId(id);
            //先获取到每一个id，分别去查询
            List<Space> select = spaceMapper.select(space);
            Space space1 = select.get(0);
            //查询到后将删除状态传入text中修改即可
            space1.setStatus(SpaceConstant.DELETE);
            spaceMapper.update(space1);
        }
    }

    /**
     * 根据字段查询文件
     * @param space
     * @return
     */
    @Override
    public List<Space> select(Space space) {
        List<Space> spaceList = spaceMapper.select(space);
        return spaceList;
    }

    /**
     * 分页查询
     * @param spacePageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SpacePageQueryDTO spacePageQueryDTO) {
        //开启分页查询
        PageHelper.startPage(spacePageQueryDTO.getPage(), spacePageQueryDTO.getPageSize());

        Page<SpaceVO> page = spaceMapper.pageQuery(spacePageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 找回文件
     * @param ids
     */
    @Override
    public void recover(List<Long> ids) {

        //循环遍历所有id，依次修改状态
        for(Long id : ids) {
            Space space = new Space();
            space.setId(id);
            //将获取到的id放入space中去查询
            List<Space> spaceList = spaceMapper.select(space);
            //因为id只有一个，所以获取到的也是一个，直接取集合的第一个即可
            Space space1 = spaceList.get(0);
            //传入找回文件的状态参数，即放回个人空间
            space1.setStatus(SpaceConstant.SQUARE);
            //执行修改方法
            spaceMapper.update(space1);
        }
    }

    /**
     * 修改状态为草稿箱状态
     * @param ids
     */
    @Override
    public void draft(List<Long> ids) {
        //循环遍历所有id，依次修改状态
        for(Long id : ids) {
            Space space = new Space();
            space.setId(id);
            //将获取到的id放入space中去查询
            List<Space> spaceList = spaceMapper.select(space);
            //因为id只有一个，所以获取到的也是一个，直接取集合的第一个即可
            Space space1 = spaceList.get(0);
            //传入找回文件的状态参数，即放回个人空间
            space1.setStatus(SpaceConstant.DRAFT);
            //执行修改方法
            spaceMapper.update(space1);
        }
    }

    /**
     * 彻底删除文件
     * @param id
     */
    @Override
    public void deleteFormTable(Long id) {
        spaceMapper.delete(id);
    }

    /**
     * 修改文档信息
     * @param space
     */
    @Override
    public void update(Space space) {
        //获取操作人id
        Long userId = BaseConstant.getCurrentId();
        //获取当前时间
        LocalDateTime time = LocalDateTime.now();

        //将时间和用户id传给text
        space.setUpdateTime(time);
        space.setUpdateUser(userId);

        spaceMapper.update(space);
    }
}
