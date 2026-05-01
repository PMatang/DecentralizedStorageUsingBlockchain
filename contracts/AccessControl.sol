// SPDX-License-Identifier: MIT
pragma solidity ^0.8.19;

contract AccessControl {

    struct AccessLog {
        address requester;
        uint256 fileId;
        bool    granted;
        uint256 timestamp;
    }

    AccessLog[] public logs;

    event AccessAttempt(
        address indexed requester,
        uint256 indexed fileId,
        bool granted
    );

    function logAccess(uint256 _fileId, bool _granted) public {
        logs.push(AccessLog({
            requester: msg.sender,
            fileId:    _fileId,
            granted:   _granted,
            timestamp: block.timestamp
        }));
        emit AccessAttempt(msg.sender, _fileId, _granted);
    }

    function getLog(uint256 index)
        public view
        returns (
            address requester,
            uint256 fileId,
            bool granted,
            uint256 timestamp
        )
    {
        AccessLog memory l = logs[index];
        return (l.requester, l.fileId, l.granted, l.timestamp);
    }

    function getLogCount() public view returns (uint256) {
        return logs.length;
    }
}