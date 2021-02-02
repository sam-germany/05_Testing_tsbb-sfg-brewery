package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.services.BeerService;
import guru.springframework.brewery.web.model.BeerDto;
import guru.springframework.brewery.web.model.BeerPagedList;
import guru.springframework.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeerController.class)
class BeerControllerTest {

    @MockBean
    BeerService beerService;              // it is a "interface" and on its "implemented Class" we put @Service so we can use @Mock on it

    @Autowired
    MockMvc mockMvc;        // here we are creating a default mock for any controller, above we have already defined for which controller we are creating it

    BeerDto validBeer;   // it is a normal class with no  @Annotation on it so we can only directly create an instance of it

    @BeforeEach
    void setUp() {
        validBeer = BeerDto.builder()                         // here we are directly creating an object with values for this class
                           .id(UUID.randomUUID())
                           .version(1)
                           .beerName("Beer1")
                           .beerStyle(BeerStyleEnum.PALE_ALE)
                           .price(new BigDecimal("12.99"))
                           .quantityOnHand(4)
                           .upc(123456789012L)
                           .createdDate(OffsetDateTime.now())
                           .lastModifiedDate(OffsetDateTime.now())
                           .build();
    }

    @AfterEach
    void tearDown() {
        reset(beerService);
    }



    @Test
    void testGetBeerById() throws Exception {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

        given(beerService.findBeerById(any())).willReturn(validBeer);          //(1) <-- defining that which method will be triggered

      MvcResult result =  mockMvc.perform(get("/api/v1/beer/" + validBeer.getId()))   //(2) here we are putting the same "id" of that object that we have created above
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id", is(validBeer.getId().toString())))
                .andExpect(jsonPath("$.beerName", is("Beer1")))
                .andExpect(jsonPath("$.createdDate", is(dateTimeFormatter.format(validBeer.getCreatedDate()))))
              .andReturn();

        System.out.println(result.getResponse().getContentAsString());

    }

    @DisplayName("List Ops -")
    @Nested
    public class TestListOperations {

        @Captor                             // https://howtodoinjava.com/mockito/mockito-annotations/    see here to understand @Captor
        ArgumentCaptor<String> beerNameCaptor;

        @Captor
        ArgumentCaptor<BeerStyleEnum> beerStyleEnumCaptor;

        @Captor
        ArgumentCaptor<PageRequest> pageRequestCaptor;

        BeerPagedList beerPagedList;

        @BeforeEach
        void setUp() {
            List<BeerDto> beers = new ArrayList<>();
            beers.add(validBeer);

            beers.add(BeerDto.builder().id(UUID.randomUUID())
                 .version(1)
                 .beerName("Beer4")
                 .upc(123123123122l)
                 .beerStyle(BeerStyleEnum.PALE_ALE)
                 .price(new BigDecimal("12.99"))
                 .quantityOnHand(66)
                 .createdDate(OffsetDateTime.now())
                 .lastModifiedDate(OffsetDateTime.now())
                 .build()
            );

            beerPagedList = new BeerPagedList(beers, PageRequest.of(1,1),2L);

            given(beerService.listBeers(beerNameCaptor.capture(),
                                        beerStyleEnumCaptor.capture(),
                                        pageRequestCaptor.capture())
                 ).willReturn(beerPagedList);
        }

        @DisplayName("Test list beers  no parameters")
        @Test
        void testListBeers() throws Exception {

            mockMvc.perform(get("/api/v1/beer").accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(jsonPath("$.content", hasSize(2)))
                    .andExpect(jsonPath("$.content[0].id", is(validBeer.getId().toString())));
        }
    }





}
