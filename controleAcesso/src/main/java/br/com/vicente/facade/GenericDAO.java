package br.com.vicente.facade;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.SQLException;
import java.text.Collator;
import java.text.Normalizer;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import br.com.vicente.util.DbUtils;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class GenericDAO<T> {

	protected Class<T> entityClass;
	protected Map<String, Object> parametroMap;
	//@PersistenceContext(unitName = DbUtils.PERSISTENCE_UNIT_NAME)
	protected EntityManager entityManager;
	protected final EntityManagerFactory entityManagerFactory;

	public GenericDAO() {
		/*CRIANDO O NOSSO EntityManagerFactory COM AS PORPRIEDADOS DO ARQUIVO persistence.xml */
		this.entityManagerFactory = Persistence.createEntityManagerFactory(DbUtils.PERSISTENCE_UNIT_NAME);
		this.entityManager = this.entityManagerFactory.createEntityManager();
		this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public Boolean save(T entity) {
		Boolean result = false;
		try {
		    begin();
			entityManager.persist(entity);
			commit();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			rooback();
			result = false;
		} finally {
			close();
		}
		return result;
	}

	protected Boolean delete(Object id, Class<T> classe) {
		Boolean result = false;
		try {
			begin();
			T entityToBeRemoved = entityManager.getReference(classe, id);
			entityManager.remove(entityToBeRemoved);
			commit();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			rooback();
			result = false;
		} finally {
			close();
		}
		return result;
	}


	public T update(T entity) throws SQLException {
		try {
			begin();
			entity = entityManager.merge(entity);
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rooback();
		} finally {
			close();
		}
		return entity;
	}


	public T findById(Integer id) {
		return entityManager.find(entityClass, id);
	}

	public T createOrUpdate(T entity) throws SQLException {
		try {
			begin();
			entity = entityManager.merge(entity);
			commit();
		} catch (Exception e) {
			e.printStackTrace();
			rooback();
		} finally {
			close();
		}
		return entity;
	}


	public Boolean remove(T entity) throws SQLException {
		Boolean result = false;
		try {
			begin();
			entityManager.remove(entityManager.merge(entity));
			commit();
			result = true;
		} catch (Exception e) {
			e.printStackTrace();
			rooback();
			result = false;
		} finally {
			close();
		}
		return result;
	}

	public void begin(){
		if(!entityManager.getTransaction().isActive()){
			entityManager.getTransaction().begin();
		}
	}
	
	public void close(){
		entityManager.close();
	}
	
	public void commit(){
		entityManager.getTransaction().commit();
	}
	
	public void rooback(){
		entityManager.getTransaction().rollback();
	}	

	// TODO - Criar classe Utils para a formação das queries e utiliza-la para
	// melhorar este método.
	private Query createQuery(T value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Query query = entityManager.createQuery(montarConsulta(value));
		populaParametrosDaQuery(query);
		return query;
	}


	private void populaParametrosDaQuery(Query query, Map<String, Object> parametros) {
		if (parametros != null) {
      		for (String parametro : parametros.keySet()) {
      			if (parametro != null) {
      				query.setParameter(parametro, parametros.get(parametro));
      			}
      		}
		}
	}


	private void populaParametrosDaQuery(Query query) {
		populaParametrosDaQuery(query, parametroMap);
	}


	// TODO - Capturar exceptions e lançar uma personalizada.
	private String montarConsulta(T value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		parametroMap = new HashMap<String, Object>();
		Class<?> clazz = value.getClass();
		Field[] fs = clazz.getDeclaredFields();
		String classSimpleName = clazz.getSimpleName();
		Boolean ordenacaoDesc = false, ordenacaoNome = false, ordenacaoId = false;
		String classSimpleNameLowerCase = classSimpleName.toLowerCase();
		StringBuilder queryBuilder = new StringBuilder("FROM " + classSimpleName + " " + classSimpleNameLowerCase + " ");
		Collator collator = Collator.getInstance(new Locale("pt", "BR"));
		collator.setStrength(Collator.PRIMARY);
		if (fs.length > 0) {
			for (Field f : fs) {
				if (f.getName().equalsIgnoreCase("descricao")) {
					ordenacaoDesc = true;
				}
				if (f.getName().equalsIgnoreCase("nome")) {
					ordenacaoNome = true;
				}
				if (f.getName().equalsIgnoreCase("id")) {
					ordenacaoId = true;
				}
				for (Method method : clazz.getMethods()) {
					if ((method.getName().startsWith("get")) && (method.getName().length() == (f.getName().length() + 3))) {
						if (method.getName().toLowerCase().endsWith(f.getName().toLowerCase())) {
							if (method.invoke(value) != null && !f.getType().isAssignableFrom(List.class)
								&& !f.getType().isAssignableFrom(Map.class) && !f.getType().isAssignableFrom(Set.class)
								&& !f.getType().isAssignableFrom(Queue.class) && !f.getType().isAssignableFrom(Deque.class)
								&& method.invoke(value).toString().length() > 0) {// verificão
								// para eliminar
								// coleções da
								// busca
								if (queryBuilder.indexOf("WHERE") < 0) {
									queryBuilder.append("WHERE ");
								}
								if (parametroMap.size() > 0) {
									queryBuilder.append(" AND ");
								}
								if (f.getType().getSimpleName().equalsIgnoreCase("string")) {
									queryBuilder.append("TiraAcento (UPPER(" + classSimpleNameLowerCase + "." + f.getName() + ")) LIKE :"
										+ f.getName());
									parametroMap.put(f.getName(), "%"
										+ Normalizer.normalize(method.invoke(value).toString().toUpperCase(), Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
										+ "%");
								} else {
									queryBuilder.append(classSimpleNameLowerCase + "." + f.getName() + " = :" + f.getName());
									parametroMap.put(f.getName(), method.invoke(value));
								}
							}
						}
					}
				}
			}
		}
		if (ordenacaoDesc && ordenacaoNome && ordenacaoId) {
			queryBuilder.append(" ORDER BY nome, descricao, id");
		} else if (ordenacaoDesc || ordenacaoNome || ordenacaoId) {
			queryBuilder.append(" ORDER BY ");
			if (ordenacaoDesc) {
				queryBuilder.append(" descricao ");
			} else if (ordenacaoNome) {
				queryBuilder.append(" nome ");
			} else if (ordenacaoId) {
				queryBuilder.append(" id ");
			}
		}
		return queryBuilder.toString();
	}
	
	/**
	 * Este método só deve ser usado na paginação que utiliza namedQuery. Serve para contar o total de resultados da query
	 * @param namedQuery
	 * @param map
	 * @param initValue
	 * @param limit
	 * @return
	 */
	public int count(String namedQuery, HashMap<String, Object> map) {
		TypedQuery<Long> query = entityManager.createNamedQuery(namedQuery, Long.class);
		populaParametrosDaQuery(query, map);
		return query.getSingleResult().intValue();
	}


	public List<T> createNamedQuery(String namedQuery, HashMap<String, Object> map) {
		return createNamedQuery(namedQuery, map, null, null);
	}
	
	public T createNamedQuerySingle(String namedQuery, HashMap<String, Object> map) {
		TypedQuery<T> query = entityManager.createNamedQuery(namedQuery, entityClass);
		populaParametrosDaQuery(query, map);
		try {
				return (T) query.getSingleResult();
			} catch (NoResultException e) {
				return null;
			} catch (NonUniqueResultException e) {
				return null;
			}
	}
	
	public int executeQuery(String namedQuery, HashMap<String, Object> map) {
		Query query = entityManager.createQuery(namedQuery);
		populaParametrosDaQuery(query, map);
		return query.executeUpdate();
	}
	
	public int executeNamedQuery(String namedquery, HashMap<String, Object> map) {
		Query query = entityManager.createNamedQuery(namedquery);
		populaParametrosDaQuery(query, map);
		return query.executeUpdate();
	}
	
	public List<T> createNamedQuery(String namedQuery, HashMap<String, Object> map, Integer initValue, Integer limit) {
		TypedQuery<T> query = entityManager.createNamedQuery(namedQuery, entityClass);
		populaParametrosDaQuery(query, map);
		if(initValue != null)
			query.setFirstResult(initValue);
		if(limit != null)
			query.setMaxResults(limit);
		List<T> list = query.getResultList();
		return list;
	}

	public List<T> executeNativeQuery(String querySQL) {
		return executeNativeQuery(querySQL, null);
	}
	
	public List<T> executeSQLQuery(String querySQL) {
		Query query = entityManager.createNativeQuery(querySQL);
		List<T> list = query.getResultList();
		return list;
	}

	public List<T> executeJPQLQuery(String querySQL) {
		Query query = entityManager.createQuery(querySQL);
		List<T> list = query.getResultList();
		return list;
	}

	public List<T> executeNativeQuery(String querySQL, HashMap<String, Object> parametros) {
		return executeNativeQuery(querySQL, parametros, null, null);
	}

	public List<T> executeNativeQuery(String querySQL, HashMap<String, Object> parametros, Integer initValue, Integer limit) {
		Query query = entityManager.createNativeQuery(querySQL, entityClass);
		populaParametrosDaQuery(query, parametros);
		if(initValue != null)
			query.setFirstResult(initValue);
		if(limit != null)
			query.setMaxResults(limit);
		List<T> list = query.getResultList();
		return list;
	}

	public T executeNativeQuerySingleResult(String querySQL) {
		Query query = entityManager.createNativeQuery(querySQL, entityClass);
		return (T) query.getSingleResult();
	}


	/**
	 * Este método realiza pesquisa no banco baseado nos atributos preenchidos
	 * na enitdade passada como parâmetro. Os atributos do tipo {@link List} e
	 * {@link }
	 * 
	 * @param value
	 * @return
	 * @return
	 * @author - Natanael 16/01/2014 - 08:23:21
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public List<T> findAll(T value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (value != null) {
			return createQuery(value).getResultList();
		} else {
			return null;
		}
	}


	public List<T> findAllDataTable(T value, int initValue, int limit) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Query query = createQuery(value);
		query.setFirstResult(initValue);
		query.setMaxResults(limit);
		return query.getResultList();
	}


	public int count(T value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		StringBuilder stringBuilder = new StringBuilder();
		Class<?> clazz = value.getClass();
		String classSimpleName = clazz.getSimpleName();
		stringBuilder.append("SELECT COUNT (");
		stringBuilder.append(classSimpleName.toLowerCase());
		stringBuilder.append(".id) ");
		stringBuilder.append(montarConsulta(value).toString());
		Query query = entityManager.createQuery(stringBuilder.toString());
		Set<String> parametros = parametroMap.keySet();
		for (String parametro : parametros) {
			if (parametro != null) {
				query.setParameter(parametro, parametroMap.get(parametro));
			}
		}
		return ((Long) query.getSingleResult()).intValue();
	}
	
	public T findSingleResult(T value) throws NonUniqueResultException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (value != null) {
			try {
				return (T) createQuery(value).getSingleResult();
			} catch (NoResultException e) {
				return null;
			} catch (NonUniqueResultException e) {
				throw new NonUniqueResultException("Consulta não retorna resultado único");
			}
		} else {
			return null;
		}
	}
	
	
	public List<T> findAll() {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery cq = cb.createQuery();
		Root<T> c = cq.from(entityClass);
		cq.select(c);
		Field[] fs = entityClass.getDeclaredFields();
		for (Field f : fs) {
			if (f.getName().equalsIgnoreCase("descricao")) {
				cq.orderBy(cb.asc(c.get("descricao")));
				break;
			}
		}
		return entityManager.createQuery(cq).getResultList();
	}


	public List<T> search(Map<Object, Object> filtros) {
		// TODO mudar implementação para pesquisar pro atributo preenchido
		// dentro da entidade usando reflexão.
		CriteriaQuery cq = entityManager.getCriteriaBuilder().createQuery();
		cq.select(cq.from(entityClass));
		return null;
	}
}
