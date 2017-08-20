package br.com.vicente.controller;

import io.jsonwebtoken.Claims;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import br.com.vicente.dao.AcessoDAO;
import br.com.vicente.http.Acesso;
import br.com.vicente.util.JwtUtil;
import br.com.vicente.entity.AcessoEntity;

/**
 * Essa classe vai expor os nosso métodos para serem acessasdos via http
 * 
 * @Path - Caminho para a chamada da classe que vai representar o nosso serviço
 * */
@Path("/acessoService")
public class acessoService {

	private final AcessoDAO dao = new AcessoDAO();

	/**
	 * @Consumes - determina o formato dos dados que vamos postar
	 * @Produces - determina o formato dos dados que vamos retornar
	 * 
	 *           Esse método cadastra uma nova acesso
	 * */
	@POST
	@Consumes("application/json")
	@Produces("application/json")
	@Path("/cadastrar")
	public String Cadastrar(Acesso acesso) {
		AcessoEntity entity = new AcessoEntity();
		String resultado = new String();
		try {
			entity = preencherAcesso(entity, acesso);
			Boolean result = dao.salvar(entity);
			if (result != null && result) {
				resultado = "Registro cadastrado com sucesso!";
			} else {
				resultado = "Erro ao tentar cadastrar o registr!";
			}
		} catch (Exception e) {
			resultado = "Erro ao cadastrar o registro: " + e.getMessage();
		}
		return resultado;
	}

	/**
	 * Essse método altera uma acesso já cadastrada
	 * **/
	@PUT
	@Produces("application/json; charset=UTF-8")
	@Consumes("application/json; charset=UTF-8")
	@Path("/alterar")
	public String Alterar(Acesso acesso) {
		AcessoEntity entity = new AcessoEntity();
		try {
			entity = preencherAcesso(entity, acesso);
			dao.alterar(entity);
			return "Registro alterado com sucesso!";
		} catch (Exception e) {
			return "Erro ao tentar alterar o registro: " + e.getMessage();
		}
	}

	/**
	 * Esse método lista todas acessos cadastradas na base
	 * */
	@GET
	@Produces("application/json; charset=UTF-8")
	@Path("/todosAcessos")
	public List<Acesso> TodasAcessos() {
		List<Acesso> acessos = new ArrayList<Acesso>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		List<AcessoEntity> listaEntityAcessos = dao.todosAcessos(map, "listaTodosAcesso");
		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); 
		for (AcessoEntity entity : listaEntityAcessos) {
			Acesso acesso = new Acesso();
			acesso.setMacCliente(entity.getMacCliente());
			acesso.setSenha(entity.getSenha());
			acesso.setToken(entity.getToken());
			acesso.setUsuario(entity.getUsuario());
			acesso.setDataInicioAcesso(fmt.format(entity.getDataInicioAcesso()));
			acesso.setDataFinalAcesso(fmt.format(entity.getDataFinalAcesso()));
			acesso.setId(entity.getId());
			acessos.add(acesso);
		}
		return acessos;
	}

	/**
	 * Esse método busca uma acesso cadastrada pelo código
	 * */
	@GET
	@Produces("application/json; charset=UTF-8")
	@Path("/getAcesso/{codigo}")
	public Acesso getAcesso(@PathParam("codigo") Integer codigo) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("id", new Long(codigo));
		AcessoEntity entity = dao.getAcesso(map, "getAcesso");
		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy"); 
		if (entity != null && entity.getId() != null) {
			return new Acesso(entity.getId(), entity.getUsuario(),
					entity.getSenha(), entity.getToken(),
					entity.getMacCliente(), fmt.format(entity.getDataInicioAcesso()),
					fmt.format(entity.getDataFinalAcesso()));
		}
		return null;
	}
	
	
	
	/**
	 * Esse método busca uma acesso cadastrada pelo código
	 * */
	@GET
	@Produces("application/json; charset=UTF-8")
	@Path("/teste")
	public String teste() {
		return "teste";
	}
	
	/**
	 * Esse método busca uma acesso cadastrada pelo usuario, senha e mac
	 * */
	@POST
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@Path("/retornarAcessoSistema")
	public Acesso retornarAcessoSistema(Acesso acesso) {
		String chaveToken = new String();
		String token = new String();
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("usuario", acesso.getUsuario());
		map.put("senha", acesso.getSenha());
		map.put("mac", acesso.getMacCliente());
		
		AcessoEntity entity = dao.getAcesso(map, "retornarAcessoSistema");
		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy"); 
		
		if (entity != null && entity.getId() != null) {
			Date data = new Date();
			String dataAtualFormatada = fmt.format(data);
			String dataFinalFormatada = fmt.format(entity.getDataFinalAcesso());
			
			try {
				Date dataAtual = fmt.parse(dataAtualFormatada);
				Date dataFinal = fmt.parse(dataFinalFormatada);
				
				Long dataTimeAtual = dataAtual.getTime();
				Long dataTimeFinal = dataFinal.getTime();
						
				if(dataTimeAtual.compareTo(dataTimeFinal) > 0){
					return new Acesso();
				}else{	
					Calendar date = Calendar.getInstance();
					date.setTime(new Date());
					date.add(Calendar.MINUTE, 30);
					
					chaveToken = new JwtUtil().gerarChaveAleatoria();
				    token = new JwtUtil().createJWT(acesso.getUsuario(), acesso.getSenha(), acesso.getMacCliente(), date.getTimeInMillis() , chaveToken);
					
					entity.setToken(token);
					entity.setChaveToken(chaveToken);
					dao.update(entity);
					
					return new Acesso(token);
				}
			} catch (ParseException e) {
				return new Acesso();
			}catch (Exception e) {
				return new Acesso();
		}
		}
		return new Acesso();
	}
	
	
	/**
	 * Esse método busca uma acesso cadastrada pelo usuario, senha e mac
	 * */
	@POST
	@Consumes("application/json; charset=UTF-8")
	@Produces("application/json; charset=UTF-8")
	@Path("/pesquisarAcessoSistema")
	public Acesso pesquisarAcessoSistema(Acesso acesso) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("usuario", acesso.getUsuario());
		map.put("senha", acesso.getSenha());
		map.put("mac", acesso.getMacCliente());
		map.put("token", acesso.getToken());

		AcessoEntity entity = dao.getAcesso(map, "pesquisarAcessoSistema");

		if (entity != null && entity.getId() != null) {
			try {
				Claims claims = new JwtUtil().parseJWT(entity.getToken(),
						entity.getChaveToken());
				if (!(acesso.getUsuario().equals(claims.getId())
						&& acesso.getSenha().equals(claims.getIssuer()) && acesso
						.getMacCliente().equals(claims.getSubject()))) {
					acesso = new Acesso();
				}
				return acesso;
			} catch (Exception e) {
				return new Acesso();
			}
		}
		return new Acesso();
	}

	/**
	 * Excluindo uma acesso pelo código
	 * */
	@DELETE
	@Produces("application/json; charset=UTF-8")
	@Path("/excluir/{codigo}")
	public String Excluir(@PathParam("codigo") Integer codigo) {
		String resultado = new String();
		try {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("id", new Long(codigo));
			Boolean result = dao.excluir(map, "getAcesso");
			if (result != null && result) {
				resultado = "Registro excluido com sucesso!";
			} else {
				resultado = "Erro ao tentar excluir o registro!";
			}
		} catch (Exception e) {
			resultado =  "Erro ao excluir o registro: " + e.getMessage();
		}
		return resultado;
	}

	public AcessoEntity preencherAcesso(AcessoEntity entity, Acesso acesso) throws ParseException {
		SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); 
		entity.setMacCliente(acesso.getMacCliente());
		entity.setSenha(acesso.getSenha());
		entity.setId(acesso.getId());
		entity.setToken(acesso.getToken());
		entity.setUsuario(acesso.getUsuario());
		entity.setDataInicioAcesso(fmt.parse(acesso.getDataInicioAcesso()));
		entity.setDataFinalAcesso(fmt.parse(acesso.getDataFinalAcesso()));
		return entity;
	}
	
	

}
