package com.ruizton.main.dao;

import static org.hibernate.criterion.Example.create;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.ruizton.main.Enum.UserGradeEnum;
import com.ruizton.main.dao.comm.HibernateDaoSupport;
import com.ruizton.main.model.Fuser;

/**
 	* A data access object (DAO) providing persistence and search support for Fuser entities.
 			* Transaction control of the save(), update() and delete() operations 
		can directly support Spring container-managed transactions or they can be augmented	to handle user-managed Spring transactions. 
		Each of these methods provides additional information for how to configure it for the desired type of transaction control. 	
	 * @see com.ruizton.main.model.Fuser
  * @author MyEclipse Persistence Tools 
 */
@Repository
public class FuserDAO extends HibernateDaoSupport  {
	     private static final Logger log = LoggerFactory.getLogger(FuserDAO.class);
		//property constants
	public static final String FLOGIN_NAME = "floginName";
	public static final String FLOGIN_PASSWORD = "floginPassword";
	public static final String FTRADE_PASSWORD = "ftradePassword";
	public static final String FNICK_NAME = "fnickName";
	public static final String FREAL_NAME = "frealName";
	public static final String FTELEPHONE = "ftelephone";
	public static final String FEMAIL = "femail";
	public static final String FIDENTITY_NO = "fidentityNo";
	public static final String FGOOGLE_AUTHENTICATOR = "fgoogleAuthenticator";
	public static final String FMOBIL_MESSAGE_CODE = "fmobilMessageCode";
	public static final String FQQ_VERIFY_CODE = "fqqVerifyCode";
	public static final String FWEIBO_VERIFY_CODE = "fweiboVerifyCode";
	public static final String FSTATUS = "fstatus";
	public static final String FIS_TEL_VALIDATE = "fisTelValidate";
	public static final String FIS_MAIL_VALIDATE = "fisMailValidate";
	public static final String FGOOGLE_VALIDATE = "fgoogleValidate";


    
    public void save(Fuser transientInstance) {
        log.debug("saving Fuser instance");
        try {
            getSession().save(transientInstance);
            log.debug("save successful");
        } catch (RuntimeException re) {
            log.error("save failed", re);
            throw re;
        }
    }
    
	public void delete(Fuser persistentInstance) {
        log.debug("deleting Fuser instance");
        try {
            getSession().delete(persistentInstance);
            log.debug("delete successful");
        } catch (RuntimeException re) {
            log.error("delete failed", re);
            throw re;
        }
    }
    
    public Fuser findById( java.lang.String id) {
        log.debug("getting Fuser instance with id: " + id);
        try {
            Fuser instance = (Fuser) getSession()
                    .get("com.ruizton.main.model.Fuser", id);
            return instance;
        } catch (RuntimeException re) {
            log.error("get failed", re);
            throw re;
        }
    }
    
    
    public List<Fuser> findByExample(Fuser instance) {
        log.debug("finding Fuser instance by example");
        try {
            List<Fuser> results = (List<Fuser>) getSession()
                    .createCriteria("com.ruizton.main.model.Fuser")
                    .add( create(instance) )
            .list();
            log.debug("find by example successful, result size: " + results.size());
            return results;
        } catch (RuntimeException re) {
            log.error("find by example failed", re);
            throw re;
        }
    }    
    
    public List findByProperty(String propertyName, Object value) {
      log.debug("finding Fuser instance with property: " + propertyName
            + ", value: " + value);
      try {
         String queryString = "from Fuser as model where model." 
         						+ propertyName + "= ?";
         Query queryObject = getSession().createQuery(queryString);
		 queryObject.setParameter(0, value);
		 return queryObject.list();
      } catch (RuntimeException re) {
         log.error("find by property name failed", re);
         throw re;
      }
	}
    
    public List findByDate(String propertyName, Date value) {
        log.debug("finding Fuser instance with Date: " + propertyName
              + ", value: " + value);
        try {
           String queryString = "from Fuser as model where model." 
           						+ propertyName + ">= ?";
           Query queryObject = getSession().createQuery(queryString);
  		   queryObject.setParameter(0, value);
  		 return queryObject.list();
        } catch (RuntimeException re) {
           log.error("find by property name failed", re);
           throw re;
        }
  	}

	public List<Fuser> findByFloginName(Object floginName
	) {
		return findByProperty(FLOGIN_NAME, floginName
		);
	}
	
	public List<Fuser> findByFloginPassword(Object floginPassword
	) {
		return findByProperty(FLOGIN_PASSWORD, floginPassword
		);
	}
	
	public List<Fuser> findByFtradePassword(Object ftradePassword
	) {
		return findByProperty(FTRADE_PASSWORD, ftradePassword
		);
	}
	
	public List<Fuser> findByFnickName(Object fnickName
	) {
		return findByProperty(FNICK_NAME, fnickName
		);
	}
	
	public List<Fuser> findByFrealName(Object frealName
	) {
		return findByProperty(FREAL_NAME, frealName
		);
	}
	
	public List<Fuser> findByFtelephone(Object ftelephone
	) {
		return findByProperty(FTELEPHONE, ftelephone
		);
	}
	
	public List<Fuser> findByFemail(Object femail
	) {
		return findByProperty(FEMAIL, femail
		);
	}
	
	public List<Fuser> findByFidentityNo(Object fidentityNo
	) {
		return findByProperty(FIDENTITY_NO, fidentityNo
		);
	}
	
	public List<Fuser> findByFgoogleAuthenticator(Object fgoogleAuthenticator
	) {
		return findByProperty(FGOOGLE_AUTHENTICATOR, fgoogleAuthenticator
		);
	}
	
	public List<Fuser> findByFmobilMessageCode(Object fmobilMessageCode
	) {
		return findByProperty(FMOBIL_MESSAGE_CODE, fmobilMessageCode
		);
	}
	
	public List<Fuser> findByFqqVerifyCode(Object fqqVerifyCode
	) {
		return findByProperty(FQQ_VERIFY_CODE, fqqVerifyCode
		);
	}
	
	public List<Fuser> findByFweiboVerifyCode(Object fweiboVerifyCode
	) {
		return findByProperty(FWEIBO_VERIFY_CODE, fweiboVerifyCode
		);
	}
	
	public List<Fuser> findByFstatus(Object fstatus
	) {
		return findByProperty(FSTATUS, fstatus
		);
	}
	

	public List findAll() {
		log.debug("finding all Fuser instances");
		try {
			String queryString = "from Fuser";
	         Query queryObject = getSession().createQuery(queryString);
			 return queryObject.list();
		} catch (RuntimeException re) {
			log.error("find all failed", re);
			throw re;
		}
	}
	
    public Fuser merge(Fuser detachedInstance) {
        log.debug("merging Fuser instance");
        try {
            Fuser result = (Fuser) getSession()
                    .merge(detachedInstance);
            log.debug("merge successful");
            return result;
        } catch (RuntimeException re) {
            log.error("merge failed", re);
            throw re;
        }
    }

    public void attachDirty(Fuser instance) {
        log.debug("attaching dirty Fuser instance");
        try {
            getSession().saveOrUpdate(instance);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
    
    public void attachClean(Fuser instance) {
        log.debug("attaching clean Fuser instance");
        try {
            getSession().lock(instance, LockMode.NONE);
            log.debug("attach successful");
        } catch (RuntimeException re) {
            log.error("attach failed", re);
            throw re;
        }
    }
    

	public List<Fuser> list(int firstResult, int maxResults,
			String filter,boolean isFY) {
		List<Fuser> list = null;
		log.debug("finding Fuser instance with filter");
		try {
			String queryString = "from Fuser "+filter;
			Query queryObject = getSession().createQuery(queryString);
			if(isFY){
				queryObject.setFirstResult(firstResult);
				queryObject.setMaxResults(maxResults);
			}
			list = queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by filter name failed", re);
			throw re;
		}
		return list;
	}
	
	public List getUserGroup(String filter) {
		List<Fuser> list = null;
		log.debug("finding Fuser instance with filter");
		try {
			String queryString = "select count(*) cou,DATE_FORMAT(fregistertime,'%Y-%c-%d') fregistertime from " +
					"fuser "+filter+" group by DATE_FORMAT(fregistertime,'%Y-%c-%d')";
			SQLQuery sqlQuery = getSession().createSQLQuery(queryString);
			list = sqlQuery.list();
		} catch (RuntimeException re) {
			log.error("find by filter name failed", re);
			throw re;
		}
		
		return list;
	}
	
	public List<Fuser> findByMap(Map<String, Object> param) {
        log.debug("getting Fuser instance with param");
        try {
            StringBuffer queryString = new StringBuffer("from Fuser as model where " ) ;
            Object[] keys = null ;
            if(param!=null){
            	keys = param.keySet().toArray() ;
            	for (Object object : keys) {
					String keystr = (String)object ;
					queryString.append(keystr+"= ? and ") ;
				}
            	
            }

            queryString.append(" 1=1 ") ;
            
            Query queryObject = getSession().createQuery(queryString.toString());
            if(keys!=null){
            	for (int i=0;i<keys.length;i++) {
					Object value = param.get(keys[i]) ;
					queryObject.setParameter(i, value) ;
				}
            }
   		 return queryObject.list();
         } catch (RuntimeException re) {
            log.error("find by property name failed", re);
            throw re;
         }
    }
	
	public List<Fuser> listUserForAudit(int firstResult, int maxResults,
			String filter,boolean isFY) {
		List<Fuser> list = null;
		log.debug("finding Fuser instance with filter");
		try {
			String queryString = "from Fuser "+filter;
			Query queryObject = getSession().createQuery(queryString);
			if(isFY){
				queryObject.setFirstResult(firstResult);
				queryObject.setMaxResults(maxResults);
			}
			list = queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by filter name failed", re);
			throw re;
		}
		return list;
	}
	
	
	public List listUsers(int firstResult, int maxResults,
			String filter,boolean isFY) {
		List list = null;
		StringBuffer sf = new StringBuffer();
		sf.append("select * from (select \n");
		sf.append("b.fid, \n");
		sf.append("b.floginName, \n");
		sf.append("b.fRealName, \n");
		sf.append("b.fTelephone, \n");
		sf.append("case b.fgrade \n");
		sf.append("when 1 then '有效用户' \n");
		sf.append("when 2 then '金豆初级玩家' \n");
		sf.append("when 3 then '豆资深玩家' \n");
		sf.append("when 4 then '金豆圈子成员' \n");
		sf.append("when 5 then '金豆圈子领袖' \n");
		sf.append("ELSE '无' \n");
		sf.append("end fownerGrade, \n");
		sf.append("count(a.fid) total, \n");
		sf.append("sum(case when a.fgrade=1 then 1 ELSE 0 end) level1, \n");
		sf.append("sum(case when a.fgrade=2 then 1 ELSE 0 end) level2, \n");
		sf.append("sum(case when a.fgrade=3 then 1 ELSE 0 end) level3, \n");
		sf.append("sum(case when a.fgrade=4 then 1 ELSE 0 end) level4, \n");
		sf.append("sum(case when a.fgrade=5 then 1 ELSE 0 end) level5, \n");
		sf.append("sum(case when a.fgrade IS NULL then 1 ELSE 0 end) \n");
		sf.append(" from fuser a LEFT outer  \n");
		sf.append("join fuser b on a.fIntroUser_id=b.fid where b.floginname is not null \n");
		sf.append("GROUP BY b.fid,b.floginName,b.fRealName,b.fTelephone,b.fgrade)as t \n");
		sf.append(filter+" \n");
		try {
			Query queryObject = getSession().createSQLQuery(sf.toString());
			if(isFY){
				queryObject.setFirstResult(firstResult);
				queryObject.setMaxResults(maxResults);
			}
			list = queryObject.list();
		} catch (RuntimeException re) {
			log.error("find by filter name failed", re);
			throw re;
		}
		return list;
	}
//	uid	账户	姓名
//	累计充值金额
//	累计提现金额
//	账户可用金额
//	账户可用冻结金额
//	其中推荐奖励金额
//	账户可用金豆
//	账户冻结金豆
//	其中推荐奖励金豆
//	其中种树奖励金豆
//	交易（买）总金额
//	交易（买）总金豆
//	交易（卖）总金额
//	交易（卖）总金豆
//	累计充值金额-累计提现金额-账户可用金额-账户可用冻结金额
//	（累计充值金额-累计提现金额-账户可用金额-账户可用冻结金额）/(账户可用金豆+账户冻结金豆)
	public List<Map> getAllUserInfo(int firstResult, int maxResults,
 			String filter,boolean isFY) {
		List<Map> all = new ArrayList();
			StringBuffer sf = new StringBuffer();
			sf.append("select * from (  \n");
			sf.append("		select   \n");
			sf.append("		a.fid ID,  \n");
			sf.append("		a.floginName name,  \n");
			sf.append("		a.frealName realName,  \n");
			sf.append("		a.fgrade grade,  \n");
			sf.append("		m.fgroupqty buyQty,  \n");
			sf.append("		a.fzhongdouqty zhongQty,  \n");
			sf.append("		d.fborrowCny borrowCny,  \n");
			sf.append("		e.fborrowBtc borrowDou,  \n");
			sf.append("		d.fcanLendCny+d.ffrozenLendCny+d.falreadyLendCny lendCny,  \n");
			sf.append("		e.fcanlendBtc+e.ffrozenLendBtc+e.falreadyLendBtc lendDou,  \n");
			sf.append("		round(IFNULL(b.t,0)+IFNULL(l.amt3,0),2) inAmt,  \n");
			sf.append("		round(IFNULL(c.t,0),2) outAmt,  \n");
			sf.append("		round(d.fTotalRMB,2) totalAmt,  \n");
			sf.append("		round(d.fFrozenRMB,2) frozenAmt,  \n");
			sf.append("		round(IFNULL(h.amt2,0),2) introlAmt,  \n");
			sf.append("		round(e.ftotal,2) totalDou , \n");
			sf.append("		round(e.ffrozen,2) frozenDou, \n");
			sf.append("		ROUND(IFNULL(i.qty,0)+ifnull(j.fharvestqty,0),2) introlDou, \n");
			sf.append("		ROUND(IFNULL(k.fharvestqty1,0),2) zhongDou, \n");
			sf.append("		f.amt tradeBuyAmt, \n");
			sf.append("		f.dou tradeBuyDou, \n");
			sf.append("		g.amt1 tradeSellAmt, \n");
			sf.append("		g.dou1 tradeSellDou, \n");
			sf.append("		round((IFNULL(b.t,0)+IFNULL(l.amt3,0)-IFNULL(c.t,0)-d.fTotalRMB-d.fFrozenRMB),2) totalGet1, \n");
			sf.append("		round((IFNULL(b.t,0)+IFNULL(l.amt3,0)-IFNULL(c.t,0)-d.fTotalRMB-d.fFrozenRMB)/(e.ftotal+e.ffrozen),2) totalGet2 \n");
			sf.append("		from fuser a LEFT OUTER JOIN   \n");
			sf.append("		(SELECT FUs_fId,SUM(fAmount) t from fcapitaloperation where fType=1 and fStatus=3 GROUP BY FUs_fId)as b  \n");
			sf.append("		on a.fid = b.fus_fid  \n");
			sf.append("		LEFT OUTER JOIN  \n");
			sf.append("		(SELECT FUs_fId,SUM(fAmount) t from fcapitaloperation where fType=2 and fStatus=3 GROUP BY FUs_fId)as c \n"); 
			sf.append("		on a.fid = c.fus_fid  \n");
			sf.append("		LEFT OUTER JOIN fwallet d on a.FWa_fId=d.fid  \n");
			sf.append("		LEFT OUTER JOIN (select * from fvirtualwallet where fVi_fId=1)as e on a.fId=e.fuid  \n");
			sf.append("	LEFT OUTER JOIN ( \n");
			sf.append("	SELECT fus_fid,ROUND(SUM(fsuccessAmount),2) amt,ROUND(SUM(fCount-fleftCount),2) dou from fentrust where fStatus in(2,3,4) and fentrustType =0 \n");
			sf.append("	GROUP BY FUs_fId \n");
			sf.append("	)as f on a.fid = f.fus_fid \n");
			sf.append("		LEFT OUTER JOIN ( \n");
			sf.append("		SELECT fus_fid,ROUND(SUM(fsuccessAmount),2) amt1,ROUND(SUM(fCount-fleftCount),2) dou1 from fentrust where fStatus in(2,3,4) and fentrustType =1 \n");
			sf.append("		GROUP BY FUs_fId \n");
			sf.append("		)as g on a.fid = g.fus_fid \n");
			sf.append("		LEFT OUTER JOIN ( \n");
			sf.append("		SELECT SUM(ftotalAmt) amt2,fuid from fdeduct GROUP BY fuid \n");
			sf.append("		)as h on a.fid = h.fuid \n");
			sf.append("		LEFT OUTER JOIN( \n");
			sf.append("		SELECT ROUND(SUBSTR(FContent,locate('金豆:',FContent)+3,locate('个！',FContent)-locate('金豆:',FContent)-3),2) qty,FReceiverId  \n");
			sf.append("		from fmessage where FTitle in('好友租用土地奖励通知','好友翻新土地奖励通知') \n");
			sf.append("		GROUP BY FReceiverId \n");
			sf.append("		)as i on a.fid = i.FReceiverId \n");
			sf.append("		LEFT OUTER JOIN ( \n");
			sf.append("		SELECT fintroluserid,SUM(fharvestqty) fharvestqty from fgameharvestlog where ftype=1 GROUP BY fintroluserid \n");
			sf.append("		)as j on a.fid = j.fintroluserid \n");
			sf.append("		LEFT OUTER JOIN( \n");
			sf.append("		SELECT fuserid,SUM(fharvestqty) fharvestqty1 from fgameharvestlog where ftype=0 GROUP BY fuserid \n");
			sf.append("		)as k on a.fid = k.fuserid \n");
			sf.append("	left outer join ( \n");
			sf.append("		SELECT fuid,SUM(famount) amt3 from foperationlog where fstatus=2 GROUP BY fuid	 \n");
			sf.append("	)as l on a.fid = l.fuid \n");
			sf.append(" left outer join fscore m on a.fscoreid=m.fid \n");
			sf.append("		)as t \n");
			sf.append(filter+" \n");
			Query queryObject = getSession().createSQLQuery(sf.toString());
			if(isFY){
				queryObject.setFirstResult(firstResult);
				queryObject.setMaxResults(maxResults);
			}
			List list = queryObject.list();
			for(int i=0;i<list.size();i++){
				Object[] o = (Object[])list.get(i);
				Map<String,Object> map = new HashMap<String,Object>();
				if(o[0] == null) continue;
				map.put("ID", o[0]);
				map.put("name", o[1]);
				map.put("realName", o[2]);
				map.put("grade", UserGradeEnum.getEnumString(Integer.parseInt(o[3].toString())));
				map.put("buyQty", o[4]);
				map.put("zhongQty", o[5]);
				map.put("borrowCny", o[6]);
				map.put("borrowDou", o[7]);
				map.put("lendCny", o[8]);
				map.put("lendDou", o[9]);
				map.put("inAmt", o[10]);
				map.put("outAmt", o[11]);
				map.put("totalAmt", o[12]);
				map.put("frozenAmt", o[13]);
				map.put("introlAmt", o[14]);
				map.put("totalDou", o[15]);
				map.put("frozenDou", o[16]);
				map.put("introlDou", o[17]);
				map.put("zhongDou", o[18]);
				map.put("tradeBuyAmt", o[19]);
				map.put("tradeBuyDou", o[20]);
				map.put("tradeSellAmt", o[21]);
				map.put("tradeSellDou", o[22]);
				map.put("totalGet1", o[23]);
				map.put("totalGet2", o[24]);
                all.add(map);
			}
			return all;
    }
	
	public String getIntrolString(int fuserid) {
		StringBuffer result = new StringBuffer();
		List list = null;
		StringBuffer sf = new StringBuffer();
		sf.append("SELECT fgrade,COUNT(fid) from fuser where fIntroUser_id="+fuserid+" \n");
		sf.append("	GROUP BY fgrade \n");
		Query queryObject = getSession().createSQLQuery(sf.toString());
		list = queryObject.list();
		for(int i=0;i<list.size();i++){
			Object[] o = (Object[])list.get(i);
			if(o[0] != null){
				if(i == 0){
					result.append("您一共推荐了");
				}
				String grade = UserGradeEnum.getEnumString(Integer.parseInt(o[0].toString()));
				result.append(grade+o[1]+"个");
				if(i != list.size()-1){
					result.append(",");
				}
			}
		}
		return result+"";
	}
	
	public List getUser(int type) {
		List all = new ArrayList();
		try {
			StringBuffer sf = new StringBuffer();
			if(type ==1){
				sf.append("SELECT fEmail,qty from ( \n");
				sf.append("SELECT b.fEmail,SUM(a.fqty) qty from fintrolinfo a \n");
				sf.append("LEFT OUTER JOIN fuser b on a.fuserid=b.fid where a.fiscny=1 \n");
				sf.append("GROUP BY a.fuserid)as a ORDER BY qty desc LIMIT 0,10  \n");
			}else{
				sf.append("SELECT fEmail,fInvalidateIntroCount from fuser where fInvalidateIntroCount>0 \n");
				sf.append(" ORDER BY fInvalidateIntroCount desc LIMIT 0,10 \n");
			}

			Query queryObject = getSession().createSQLQuery(sf.toString());
			List list = queryObject.list();
			if(list != null && list.size() >0 && list.get(0) != null){
				for(int i=0;i<list.size();i++){
					Object[] o = (Object[])list.get(i);
					Map map = new HashMap(); 
					String a = o[0].toString();
					int start =4;
					if(a.indexOf("@") ==4){
						start=1;
					}
					String b = a.substring(start, a.indexOf("@"));
					map.put("userid",a.replaceAll(b, "*****"));
					map.put("qty", o[1]);
					all.add(map);
				}
			}
		} catch (RuntimeException re) {
			log.error("find by filter name failed", re);
			throw re;
		}
		return all;
	}
	
	
	
}