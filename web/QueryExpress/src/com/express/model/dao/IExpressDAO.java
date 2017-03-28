package com.express.model.dao;

import java.util.List;

import com.express.model.beans.Express;
import com.express.model.beans.ExpressGson;

public interface IExpressDAO {
/**
 * 快递表操作
 */
	// 增加快递
	public abstract boolean addExpress(Express express);
	//删除快递
	public abstract boolean delExpress(int express_id);
	//查找快递分页
	public abstract List<ExpressGson> getAll(int cur,int user_id);
	//查找某个快递
	public abstract Express getOne(int express_id);
}
