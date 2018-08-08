package com.ditp.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.Subject.Builder;
import org.apache.shiro.util.ThreadContext;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.subject.WebSubject;
import org.hibernate.Hibernate;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.ditp.dao.RedisDAO;



/**
 * 用于redis同步登陆
 *
 * @author miraclerz
 *
 */
public class RedisFilter implements Filter {

	private static final Logger logger = Logger.getLogger(RedisFilter.class);

	private  RedisDAO redisDAO;
//	private SyuserServiceI syuserServiceI;
//	private TuserServiceI tuserServiceI;
	private DefaultWebSecurityManager securityManager;

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;


//		SessionInfo sessionInfo = null;
//		if(request.getSession().getAttribute(ConfigUtil.getSessionInfoName())!=null)
//		{
//		sessionInfo=(SessionInfo)request.getSession().getAttribute(ConfigUtil.getSessionInfoName());
//		}
//
//		String requestURI = request.getRequestURI();
//         //取得url里的JSESSIONID
//    	String JSESSIONID = StringUtils.substringAfterLast(requestURI, "JSESSIONID=");
//
//        if(request.getSession().getAttribute("JSESSIONID")!=null)
//        {//如果session里的JSESSIONID不为空，表示已经登陆了，JSESSIONID就用这个了
//        	JSESSIONID=(String) request.getSession().getAttribute("JSESSIONID");
//        }
//
//		 String  userName=null;
//		if(sessionInfo==null&&JSESSIONID!=null&&!"".equals(JSESSIONID))
//		{//如果没登陆且JSESSIONID不为空，即url地址里有JSESSIONID
//          userName=redisDAO.doRead(JSESSIONID);
//          logger.info(userName+":同步登陆");
//		}
//
//     	if(sessionInfo==null&&userName!=null)
//		{
//		//HqlFilter hqlFilter = new HqlFilter();
//		//hqlFilter.addFilter("QUERY_t#loginname_S_EQ", userName);
// 		//Syuser user = syuserServiceI.getByFilter(hqlFilter);
//
// 		//HqlFilter hqlFiltert = new HqlFilter();
//		//hqlFiltert.addFilter("QUERY_t#username_S_EQ", userName);
// 		//Tuser tuser = tuserServiceI.getByFilter(hqlFiltert);
//
//		if (user != null&&tuser!=null) {
//		    sessionInfo = new SessionInfo();
//			Hibernate.initialize(user.getSyroles());
//			Hibernate.initialize(user.getSyorganizations());
//			Hibernate.initialize(user.getSyresources());
//			for (Syrole role : user.getSyroles()) {
//				Hibernate.initialize(role.getSyresources());
//			}
//			for (Syorganization organization : user.getSyorganizations()) {
//				Hibernate.initialize(organization.getSyresources());
//			}
//
//			user.setIp(IpUtil.Utils.getIp(request));
//			sessionInfo.setUser(user);


			//同步登陆shiro
		    SecurityUtils.setSecurityManager(securityManager);//
		    Builder builder = new WebSubject.Builder(request,response);
		    builder.authenticated(true);
		    Subject subject= builder.buildSubject();
//		    //设置用户的session(如果不是shiro，就直接是普通的在这里设置session就行了)
//		    subject.getSession().setAttribute(ConfigUtil.getSessionInfoName(), sessionInfo);
//		    //在session里保存登陆时的sessionid，这个sessionid会存到redis里去，本系统也会一直用这个作同步
//		    subject.getSession().setAttribute("JSESSIONID", JSESSIONID);
//		    ThreadContext.bind(subject);//线程变量中绑定一个已通过验证的Subject对象
		//}
		//}
//       if(sessionInfo!=null)
//       {
//    	   redisDAO.update(JSESSIONID,sessionInfo.getUser().getLoginname());
//	       System.out.println("同步session啦=>"+JSESSIONID);
//       }
		chain.doFilter(request, response);
	}

		public void init(FilterConfig filterConfig) throws ServletException {
		//这些是因为filter无法直接自动装载spring里的bean，于是用下面的方法也取得bean
		ServletContext context = filterConfig.getServletContext();
        ApplicationContext ctx = WebApplicationContextUtils.getWebApplicationContext(context);
        redisDAO = (RedisDAO) ctx.getBean("redisDAO");//直接以bean名称来取
        securityManager = (DefaultWebSecurityManager) ctx.getBean("securityManager");
        //syuserServiceI=(SyuserServiceI)ctx.getBean("syuserServiceImpl");
       // tuserServiceI=(TuserServiceI)ctx.getBean("tuserServiceImpl");

      /*  String[] syuserServices=ctx.getBeanNamesForType(SyuserServiceI.class);//取得所有这个接口的实现类的bean名(以接口装载的不知道bean名是啥)
        syuserServiceI = (SyuserServiceI)ctx.getBean(syuserServices[0]);//取第一个实现类名
        logger.info("实现类名:"+syuserServices[0]);
        String[] tuserServices=ctx.getBeanNamesForType(TuserServiceI.class);
        tuserServiceI = (TuserServiceI)ctx.getBean(tuserServices[0]);
*/
	}

	public void destroy() {
	}
}
