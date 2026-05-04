package com.example.MediFlow.repository.query.impl;

import com.example.MediFlow.Dtos.user_dto.AdminFilter;
import com.example.MediFlow.Dtos.user_dto.AdminResponseDto;
import com.example.MediFlow.Dtos.user_dto.UserDTO;
import com.example.MediFlow.entity.User;
import com.example.MediFlow.mapper.UserMapper;
import com.example.MediFlow.repository.UserRepository;
import com.example.MediFlow.repository.query.IUserQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class UserQueryImpl implements IUserQuery {
    private final EntityManager em;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    public UserQueryImpl(EntityManager em) {
        this.em = em;
    }
    @Override
    public AdminResponseDto getAdminPagination(int pageNo, int pageSize, String sortBy, String sortDir, AdminFilter filter) {
        AdminResponseDto GameResponse = new AdminResponseDto();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);

        Predicate[] predicatesArray = getPredicates(filter, cb, root, cq);
        cq.distinct(true);
        cq.where(predicatesArray);
        cq.orderBy(cb.desc(root.get(sortBy)));

        long count = countUserPagination(filter);

        TypedQuery<User> query = em.createQuery(cq);
        query.setFirstResult(pageNo * pageSize);
        query.setMaxResults(pageSize);

        List<User> porteFeuille = query.getResultList();



        List<UserDTO> clientDto = porteFeuille.stream()
                .map(this::convertOneToDto)
                .collect(Collectors.toList());

        GameResponse.setContent(clientDto);
        GameResponse.setPageNo(pageNo);
        GameResponse.setTotalElements(count);
        GameResponse.setPageSize(pageSize);

        return GameResponse;
    }

    public long countUserPagination(AdminFilter filter) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<User> root = cq.from(User.class);
        Predicate[] predicatesArray = getPredicates(filter, cb, root, cq);
        cq.select(cb.countDistinct(root));
        cq.where(predicatesArray);
        return em.createQuery(cq).getSingleResult();
    }

    private <T> Predicate[] getPredicates(AdminFilter filter, CriteriaBuilder cb, Root<User> root, CriteriaQuery<T> cq) {
        List<Predicate> predicates = new ArrayList<>();
        return predicates.toArray(new Predicate[0]);
    }
    private UserDTO convertOneToDto(User post) {
        UserDTO user = new UserDTO();
        user.setId(post.getId());
        user.setEmail(post.getEmail());
        user.setProfilePicture(post.getProfilePicture());
        user.setIsValidated(post.getIsValidated());
        user.setLastName(post.getLastName());
        user.setFirstName(post.getFirstName());
        user.setCreatedAt(post.getCreatedAt());
        user.setPhone(post.getPhone());
        user.setRoleTypes(post.getRoleTypes());
        return user;
    }



}
