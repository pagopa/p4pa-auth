package it.gov.pagopa.payhub.auth.utils;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Sort;

public class PageUtils {

  public static Sort convertToSort(List<String> sort) {
    List<String> parsedSort = new ArrayList<>();
    if (sort != null && !sort.isEmpty()) {
      // if in input has single sort and spring create list explode by , (es: sort=field1,asc)
      if (sort.size() == 2 && !sort.get(0).contains(",") && !sort.get(1).contains(",")) {
        parsedSort.add(sort.get(0) + "," + sort.get(1));
      } else {
        parsedSort.addAll(sort);
      }
    }
    // if in input has multiple sort and spring create list by sort key (es: sort=field1,asc&sort=field2,asc)
    Sort sortOrder = Sort.unsorted();
    if (!parsedSort.isEmpty()) {
      List<Sort.Order> orders = new ArrayList<>();
      for (String sorting : parsedSort) {
        String[] parts = sorting.split(",", 2);
        String property = parts[0].trim();
        Sort.Direction direction = (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim()))
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;
        orders.add(new Sort.Order(direction, property));
      }
      sortOrder = Sort.by(orders);
    }
    return sortOrder;
  }
}
