pragma solidity ^0.4.25;
pragma experimental ABIEncoderV2;

//印章合约
contract Seal {
    //印章信息
    struct SealInfo {
        string name;  //姓名
        string cardId;//身份证
        string datatime;//创建印章时间戳
    }

    //签章记录
    struct RecordInfo {
        string code;
        string datetime;
    }

    SealInfo public sealInfo;

    //获取账户信息
    function getSealInfo() view public returns(string,string,string) {
        return (sealInfo.name,sealInfo.cardId,sealInfo.datatime);
    }

    //签章记录（key：签章文档hash值，value：签章时间戳）
    mapping(string=>RecordInfo) recordMap;
    string[] keys; 

    //构造函数
    constructor(string _name,string _cardId,string _datetime) public {
        sealInfo.name = _name;
        sealInfo.cardId = _cardId;
        sealInfo.datatime = _datetime;
    }

    function signature(string _hash,string _code,string _datetime) public {
        recordMap[_hash] = RecordInfo(_code,_datetime);
        keys.push(_hash);
    }

    function getRecord(string _hash) view public returns(string,string) {
        RecordInfo memory recordInfo = recordMap[_hash];
        return (recordInfo.code,recordInfo.datetime);
    }

    function getAllHash() view public returns(string[] memory) {
        return keys;
    }
    
}