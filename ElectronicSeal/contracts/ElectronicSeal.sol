pragma solidity ^0.4.25;
pragma experimental ABIEncoderV2;

import "./Seal.sol";

//主合约
contract ElectronicSeal {
    address owner; //合约拥有者
    
    //key为账户地址
    mapping(address => Seal) sealMap;

    //构造函数
    constructor() public {
        owner = msg.sender;
    }

    //判断合约调用者是否为合约拥有者
    modifier onlyOwner() {
        require(owner == msg.sender,"Only owner can call");
        _;
    }

    //添加签章账户
    function addSealAccount(address _account,string _name,string _cardId,string _datetime) public onlyOwner{
        Seal seal = new Seal(_name,_cardId,_datetime);
        sealMap[_account] = seal;
    }

    //添加账户签章信息
    function sealSignature(address _account,string _hash,string _code,string _datetime) public onlyOwner{
        Seal seal = sealMap[_account];
        seal.signature(_hash, _code,_datetime);
    }

    //查看账户信息
    function getSealAccount(address _account) view public returns(string,string,string) {
        Seal seal = sealMap[_account];
        return seal.getSealInfo();
    }

    //查看账户签章信息
    function querySignature(address _account,string _hash) view public returns(string,string) {
        Seal seal = sealMap[_account];
        return seal.getRecord(_hash);
    }

    //查看账户签章hash值
    function queryAllHash(address _account) view public returns(string[] memory) {
        Seal seal = sealMap[_account];
        return seal.getAllHash();
    }
}
