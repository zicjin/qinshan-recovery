package com.sunwayworld.framework.gikam.resource.impl;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.framework.gikam.resource.GikamActionResource;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.matrix.MatrixHelper;
import com.sunwayworld.framework.support.matrix.service.GenericMatrixService;
import com.sunwayworld.framework.utils.ClassUtils;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GikamActionResourceImpl implements GikamActionResource {
   @Override
   public void instantSave(RestJsonWrapperBean wrapper) {
      List<GenericService<?, ?>> serviceList = wrapper.getBodyServiceList();
      int size = serviceList.size();

      for (int i = 0; i < size; i++) {
         GenericService service = serviceList.get(i);
         if (service != null) {
            if (GenericMatrixService.class.isAssignableFrom(ClassUtils.getRawType(service.getClass()))) {
               List<String> contentList = wrapper.getBodyList().get(i);
               if (MatrixHelper.isMatrixContent(contentList)) {
                  ((GenericMatrixService)service).save(contentList.stream().map(c -> JSON.parseObject(c)).collect(Collectors.toList()));
                  continue;
               }
            }

            service.updateIfChanged(wrapper.parse(service.getDao().getType(), i));
         }
      }
   }
}
