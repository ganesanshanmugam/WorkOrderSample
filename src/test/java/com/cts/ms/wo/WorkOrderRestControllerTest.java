package com.cts.ms.wo;

import com.cts.ms.wo.controller.WorkOrderController;
import com.cts.ms.wo.service.WorkOrderService;
import com.cts.ms.wo.vo.WorkOrder;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static com.cts.ms.wo.ServiceEndPoint.*;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = WorkEngineApp.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WorkOrderRestControllerTest {


    public static final String POST_REQUEST_CONTENT = "[{\"name\" : \"cts_1010\", \"customerid\" : \"cts\", " +
            "\"details\" : \"cleaning\", \"start_date\" : \"10/10/2017\"}]";

    public static final String POST_REQUEST_MULTIPLE_CONTENT = "[{\"name\" : \"cts_1010\", \"customerid\" : \"cts\", " +
            "\"details\" : \"cleaning\", \"start_date\" : \"10/10/2017\"}," +
            "{\"name\" : \"cts_1010\", \"customerid\" : \"cts\", " +
            "\"details\" : \"cleaning\", \"start_date\" : \"10/10/2017\"}]";


    @InjectMocks
    WorkOrderController controller;

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Mock
    private WorkOrderService workOrderService;

    private MockMvc mockMvc1;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        //Don't Delete this for reference
        this.mockMvc1 = MockMvcBuilders.webAppContextSetup(context).build();
        mockMvc = standaloneSetup(controller).build();
    }


//    @Test
//    public void thatGotIndexPageResponseWhenCallInitialPage() throws Exception {
//
//        RequestBuilder mockRequest = MockMvcRequestBuilders.get("/");
//        ResultMatcher expectedResult = status().isOk();
//        mockMvc1.perform(mockRequest)
//                .andExpect(expectedResult);
//    }


    @Test
    public void thatGotSuccessResponseWhenCallGetServiceOrderList() throws Exception {
        RequestBuilder mockRequest = MockMvcRequestBuilders.get(GET_SERVICES);
        ResultMatcher expectedResult = status().isOk();
        mockMvc.perform(mockRequest)
                .andExpect(expectedResult);
    }


    @Test
    public void thatGotServiceListWhenRequestServiceList() throws Exception {
        when(workOrderService.getWorkOrderById(0)).thenReturn(new WorkOrder(""));

        RequestBuilder getServiceRequest = MockMvcRequestBuilders.get(GET_SERVICE)
                .accept(MediaType.APPLICATION_JSON);
        ResultMatcher expectedResult = status().isOk();
        ResultMatcher expectedResult1 = jsonPath("$.name").exists();

        mockMvc.perform(getServiceRequest)
                .andExpect(expectedResult)
                .andExpect(expectedResult1);
    }


    @Test
    public void thatShouldReturnsBadRequestStatusCodeWhenBodyContainsEmptyBody() throws Exception {

        mockMvc.perform(post(POST_ORDERS)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void thatShouldReturnsBadRequestStatusCodeWhenBodyContainsEmptyObject() throws Exception {

        mockMvc.perform(post(POST_ORDERS)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void thatShouldReturnsBadRequestStatusCodeWhenBodyContainsEmptyArrayBody() throws Exception {

        mockMvc.perform(post(POST_ORDERS)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content("[]"))
                .andExpect(status().isBadRequest());

    }


    @Test
    public void thatShouldReturnsAllPropertiesWhenBodyContainsCompleteWorkOrder() throws Exception {

        List<WorkOrder> workOrderList = new ArrayList();
        workOrderList.add(new WorkOrder("cts", "cts_1011", "cleaning", "10/10/2017", ""));
        workOrderList.add(new WorkOrder("cts", "cts_1012", "cleaning", "10/10/2017", ""));
        when(workOrderService.createOrders(isA(List.class))).thenReturn(workOrderList);

        ResultMatcher expectedResult = status().isOk();
        ResultMatcher expectedResult1 = jsonPath("$[0].name").exists();
        ResultMatcher expectedResult2 = jsonPath("$[0].customerid").exists();
        ResultMatcher expectedResult3 = jsonPath("$[0].details").exists();
        ResultMatcher expectedResult4 = jsonPath("$[0].start_date").exists();

        mockMvc.perform(post(POST_ORDERS)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(POST_REQUEST_MULTIPLE_CONTENT))
                .andDo(print())
                .andExpect(expectedResult)
                .andExpect(expectedResult1)
                .andExpect(expectedResult2)
                .andExpect(expectedResult3)
                .andExpect(expectedResult4);

    }

    @Test
    public void thatShouldReturnsAllWorkOrdersWhenBodyContainsMultipleWorkOrder() throws Exception {

        List<WorkOrder> workOrderList = new ArrayList();
        workOrderList.add(new WorkOrder("cts_1010"));
        workOrderList.add(new WorkOrder("cts_1011"));
        when(workOrderService.createOrders(isA(List.class))).thenReturn(workOrderList);


        ResultMatcher expectedResult = status().isOk();
        ResultMatcher expectedResult1 = jsonPath("$", hasSize(2));
        mockMvc.perform(post(POST_ORDERS)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(POST_REQUEST_MULTIPLE_CONTENT))
                .andDo(print())
                .andExpect(expectedResult)
                .andExpect(expectedResult1).andDo(print());

    }


    @Test
    public void thatShouldReturnsBadRequestStatusCodeWhenUpdateWorkOrderRequestBodyContainsEmptyBody() throws Exception {

        mockMvc.perform(post(UPDATE_ORDERS)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());

    }

    @Test
    public void thatShouldReturnsSuccessStatusCodeWhenUpdateWorkOrderRequestBodyContainsValidWorkOrder() throws Exception {

        String CONTENT = "{\"name\" : \"cts_1010\", \"customerid\" : \"cts\", \"details\" : \"cleaning\", " +
                "\"start_date\" : \"10/10/2017\",\"status\" : \"open\"}";
        mockMvc.perform(post(UPDATE_ORDERS)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(CONTENT))
                .andExpect(status().isOk());

    }

}
