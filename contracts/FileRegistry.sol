// SPDX-License-Identifier: MIT
pragma solidity ^0.8.19;

contract FileRegistry {

    struct FileMeta {
        string  cid;           // IPFS CID or local file identifier
        string  policyJson;    // ABAC policy eg {"role":"Doctor"}
        address owner;         // wallet address of uploader
        uint256 timestamp;
    }

    mapping(uint256 => FileMeta) public files;
    uint256 public fileCount;

    event FileRegistered(
        uint256 indexed fileId,
        address indexed owner,
        string cid
    );

    function registerFile(
        string memory _cid,
        string memory _policyJson
    ) public returns (uint256) {
        fileCount++;
        files[fileCount] = FileMeta({
            cid:        _cid,
            policyJson: _policyJson,
            owner:      msg.sender,
            timestamp:  block.timestamp
        });
        emit FileRegistered(fileCount, msg.sender, _cid);
        return fileCount;
    }

    function getFile(uint256 _id)
        public view
        returns (
            string memory cid,
            string memory policyJson,
            address owner,
            uint256 timestamp
        )
    {
        FileMeta memory f = files[_id];
        return (f.cid, f.policyJson, f.owner, f.timestamp);
    }
}