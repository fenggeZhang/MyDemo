package com.express.model.beans;

import java.io.Serializable;
import java.util.List;

public class QueryResultBean implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * EBusinessID : 1266891
     * ShipperCode : STO
     * Success : true
     * LogisticCode : 3315897438827
     * State : 3
     * Traces : [{"AcceptTime":"2016-10-17 15:31:27","AcceptStation":"【收件】【江苏通州公司】的【陆飞(18068652054)】已收件,扫描员是【陆飞(18068652054)】"},{"AcceptTime":"2016-10-17 16:55:41","AcceptStation":"【收件】【江苏通州公司】的【大货扫描】已收件,扫描员是【大货扫描】"},{"AcceptTime":"2016-10-17 16:58:00","AcceptStation":"【发件】快件在【江苏通州公司】由【杨国军】扫描发往【浙江杭州中转部】"},{"AcceptTime":"2016-10-17 16:58:00","AcceptStation":"【装车】【江苏通州公司】正在进行【装车】扫描"},{"AcceptTime":"2016-10-18 05:52:56","AcceptStation":"【发件】快件在【浙江杭州中转部】由【张水立】扫描发往【浙江杭州余杭公司】"},{"AcceptTime":"2016-10-18 13:43:48","AcceptStation":"【到件】快件到达【浙江杭州余杭公司】,上一站是【】,扫描员是【扫描员45】"},{"AcceptTime":"2016-10-18 14:27:56","AcceptStation":"【派件】【浙江杭州余杭公司】的【余杭仓工】正在派件,扫描员是【扫描员44】"},{"AcceptTime":"2016-10-18 14:47:49","AcceptStation":"【派件】【浙江杭州余杭公司】的【仓工.仓前工业区 手机(15857162483)】正在派件,扫描员是【仓工.仓前工业区】"},{"AcceptTime":"2016-10-18 16:39:14","AcceptStation":"【签收】已签收,签收人是:【同事代签】"}]
     */

    private String EBusinessID;//用户id
    private String ShipperCode;//快递公司编号
    private boolean Success;//成功与否
    private String LogisticCode;//物流运单号
    private String State;//物流状态
    /**
     * AcceptTime : 2016-10-17 15:31:27
     * AcceptStation : 【收件】【江苏通州公司】的【陆飞(18068652054)】已收件,扫描员是【陆飞(18068652054)】
     */

    private List<TracesBean> Traces;//物流的行程信息

    public String getEBusinessID() {
        return EBusinessID;
    }

    public void setEBusinessID(String eBusinessID) {
        EBusinessID = eBusinessID;
    }

    public String getShipperCode() {
        return ShipperCode;
    }

    public void setShipperCode(String shipperCode) {
        ShipperCode = shipperCode;
    }

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean success) {
        Success = success;
    }

    public String getLogisticCode() {
        return LogisticCode;
    }

    public void setLogisticCode(String logisticCode) {
        LogisticCode = logisticCode;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public List<TracesBean> getTraces() {
        return Traces;
    }

    public void setTraces(List<TracesBean> traces) {
        Traces = traces;
    }

    public static class TracesBean {
        private String AcceptTime;//时间
        private String AcceptStation;//描述

        public String getAcceptTime() {
            return AcceptTime;
        }

        public void setAcceptTime(String acceptTime) {
            AcceptTime = acceptTime;
        }

        public String getAcceptStation() {
            return AcceptStation;
        }

        public void setAcceptStation(String acceptStation) {
            AcceptStation = acceptStation;
        }

		@Override
		public String toString() {
			return "TracesBean [AcceptTime=" + AcceptTime + ", AcceptStation=" + AcceptStation + "]";
		}
        
    }

	@Override
	public String toString() {
		return "QueryResultBean [EBusinessID=" + EBusinessID + ", ShipperCode=" + ShipperCode + ", Success=" + Success
				+ ", LogisticCode=" + LogisticCode + ", State=" + State + ", Traces=" + Traces.toString() + "]";
	}
    
}
